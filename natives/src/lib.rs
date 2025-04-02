use std::{
    cell::RefCell,
    collections::HashMap,
    ffi::{CStr, CString, c_char},
    fs::File,
    io::BufReader,
    str::FromStr,
    sync::{
        Once,
        mpsc::{self, Receiver, Sender},
    },
    thread::{self, JoinHandle},
    time::Duration,
};

use rodio::{Decoder, OutputStream, Sink};
use uuid::Uuid;

thread_local! {
    static PLAYER_COMMAND_SENDERS: RefCell<HashMap<String,Sender<String>>>=RefCell::new(HashMap::new());
    static PLAYER_THREAD_HANDLERS: RefCell<HashMap<String,JoinHandle<()>>>=RefCell::new(HashMap::new());
}

static INIT: Once = Once::new();

struct SoundPlayer {
    _stream: OutputStream,
    sink: Sink,
}

fn convert_c_char_ptr_to_string(v: *const c_char) -> String {
    let c_str = unsafe { CStr::from_ptr(v) };
    let rust_str = c_str.to_str().unwrap();
    rust_str.to_string()
}

fn convert_string_to_c_char_ptr(v: &str) -> *const c_char {
    let c_str = CString::from_str(v).unwrap();
    c_str.into_raw()
}

fn create_sound_player(input_filepath: &String) -> SoundPlayer {
    let (stream, handle) = OutputStream::try_default().unwrap();
    let sink = Sink::try_new(&handle).unwrap();
    sink.pause();

    let file = File::open(input_filepath).unwrap();
    sink.append(Decoder::new(BufReader::new(file)).unwrap());

    SoundPlayer {
        _stream: stream,
        sink: sink,
    }
}

///Creates a new thread for sound player and returns its unique id.
#[unsafe(no_mangle)]
pub extern "C" fn spawn_sound_player_thread(c_input_filepath: *const c_char) -> *const c_char {
    INIT.call_once(|| {
        env_logger::init();
    });

    let input_filepath = convert_c_char_ptr_to_string(c_input_filepath);

    let (sender, receiver): (Sender<String>, Receiver<String>) = mpsc::channel();
    let handle = thread::spawn(move || {
        let player = create_sound_player(&input_filepath);
        loop {
            if let Ok(command) = receiver.try_recv() {
                match command.as_str() {
                    "stop" => {
                        player.sink.stop();
                        break;
                    }
                    "play" => player.sink.play(),
                    "pause" => player.sink.pause(),
                    _ => {
                        log::warn!("Unknown command: {}", command);
                    }
                }
            }
            if player.sink.empty() {
                break;
            }

            thread::sleep(Duration::from_millis(100));
        }
    });

    let id = Uuid::new_v4().to_string();
    PLAYER_COMMAND_SENDERS.with(|m| {
        m.borrow_mut().insert(id.clone(), sender);
    });
    PLAYER_THREAD_HANDLERS.with(|m| {
        m.borrow_mut().insert(id.clone(), handle);
    });

    convert_string_to_c_char_ptr(&id)
}

///Sends a command to a sound player.
#[unsafe(no_mangle)]
pub extern "C" fn send_command_to_sound_player(
    c_id: *const c_char,
    c_command: *const c_char,
) -> i32 {
    INIT.call_once(|| {
        env_logger::init();
    });

    let id = convert_c_char_ptr_to_string(c_id);

    //Check if player thread is already finished
    let mut ret = 0;
    PLAYER_THREAD_HANDLERS.with(|m| {
        if let Some(handle) = m.borrow().get(&id) {
            //Remove sender if it's already finished
            if handle.is_finished() {
                PLAYER_COMMAND_SENDERS.with(|mm| {
                    mm.borrow_mut().remove(&id);
                });
                log::info!("Player thread is already finished: {}", &id);
                ret = 1;
            }
        } else {
            log::error!(
                "Could not find a player thread for the ID specified: {}",
                &id
            );
            ret = -1;
        }
    });
    if ret != 0 {
        return ret;
    }

    //Send command to player thread
    let command = convert_c_char_ptr_to_string(c_command);
    let mut ret = 0;
    PLAYER_COMMAND_SENDERS.with(|m| {
        if let Some(sender) = m.borrow().get(&id) {
            if let Err(e) = sender.send(command) {
                log::error!("{}", e);
                ret = -1;
            }
        }
    });

    ret
}

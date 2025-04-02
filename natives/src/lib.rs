use std::{
    cell::RefCell,
    collections::{HashMap, HashSet},
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
    static PLAYER_RESPONSE_RECEIVERS: RefCell<HashMap<String,Receiver<String>>>=RefCell::new(HashMap::new());
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

    let (command_sender, command_receiver): (Sender<String>, Receiver<String>) = mpsc::channel();
    let (response_sender, response_receiver): (Sender<String>, Receiver<String>) = mpsc::channel();
    thread::spawn(move || {
        let player = create_sound_player(&input_filepath);
        loop {
            if let Ok(command) = command_receiver.try_recv() {
                match command.as_str() {
                    "stop" => {
                        player.sink.stop();
                        break;
                    }
                    "play" => player.sink.play(),
                    "pause" => player.sink.pause(),
                    "is_finished" => {
                        let resp;
                        if player.sink.empty() {
                            resp = "true";
                        } else {
                            resp = "false";
                        }

                        if let Err(e) = response_sender.send(resp.to_string()) {
                            log::error!("{}", e);
                        }
                    }
                    _ => {
                        log::warn!("Unknown command: {}", command);
                    }
                }
            }

            thread::sleep(Duration::from_millis(100));
        }
    });

    let id = Uuid::new_v4().to_string();
    PLAYER_COMMAND_SENDERS.with(|m| {
        m.borrow_mut().insert(id.clone(), command_sender);
    });
    PLAYER_RESPONSE_RECEIVERS.with(|m| {
        m.borrow_mut().insert(id.clone(), response_receiver);
    });

    convert_string_to_c_char_ptr(&id)
}

///Sends a command to a sound player.
#[unsafe(no_mangle)]
pub extern "C" fn send_command_to_sound_player(
    c_id: *const c_char,
    c_command: *const c_char,
) -> *const c_char {
    INIT.call_once(|| {
        env_logger::init();
    });

    let id = convert_c_char_ptr_to_string(c_id);
    let command = convert_c_char_ptr_to_string(c_command);
    let mut ret = "".to_string();
    PLAYER_COMMAND_SENDERS.with(|m| {
        if let Some(sender) = m.borrow().get(&id) {
            if let Err(e) = sender.send(command.clone()) {
                log::error!("{}", e);
                ret = "error".to_string();
            }
        } else {
            log::error!(
                "Could not find a player thread for the ID specified: {}",
                &id
            );
            ret = "error".to_string();
        }
    });

    let commands_with_response: HashSet<&str> = vec!["is_finished"].into_iter().collect();
    if commands_with_response.contains(command.as_str()) {
        PLAYER_RESPONSE_RECEIVERS.with(|m| {
            if let Some(receiver) = m.borrow().get(&id) {
                match receiver.recv() {
                    Ok(resp) => ret = resp,
                    Err(e) => {
                        log::error!("{}", e);
                        ret = "error".to_string();
                    }
                }
            }
        });
    }

    convert_string_to_c_char_ptr(&ret)
}

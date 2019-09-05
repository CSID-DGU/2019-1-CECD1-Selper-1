from bluetooth import *
from flask import Flask
from flask import request
import time
import threading

key1 = None
key2 = None
app = Flask(__name__)

def blue1():
    global key1
    server_sock=BluetoothSocket(RFCOMM )
    port = 1
    server_sock.bind(("",port))
    server_sock.listen(1)
    while True:
            client_sock,address = server_sock.accept()
            print("1Accepted connection from ",address)

            while True:
                try:
                    test = key1
                    time.sleep(0.1)
                    if test == "1":
                        print("123 Accepted")
                        client_sock.send("open")
                        key1 = None
                        test = None
                    elif key1 == "pause":
                        client_sock.send("pause")
                        key1 = None
                    elif key1 == "play":
                        client_sock.send("play")
                        key1 = None
                    elif key1 == "quit":
                        client_sock.send("quit")
                        key1 = None
                    elif key1 == "next":
                        client_sock.send("next")
                        key1 = None
                    elif key1 == "previous":
                        client_sock.send("previous")
                        key1 = None
                    elif key1 == "base":
                        client_sock.send("base")
                        key1 = None
                    elif key1 == "dir":
                        client_sock.send("dir")
                        key1 = None
                except KeyboardInterrupt:
                    print("disconnected")
                    break

            client_sock.close()
            server_sock.close()
    return


def blue2():
    global key2
    server_sock=BluetoothSocket(RFCOMM )
    port = 2
    server_sock.bind(("",port))
    server_sock.listen(1)
    while True:
            client_sock,address = server_sock.accept()
            print("2Accepted connection from ",address)

            while True:
                try:
                    if key2 == 1:
                        print("2 Accepted")
                        client_sock.send("open")
                        key2 = None
                    elif key2 == "pause":
                        client_sock.send("pause")
                        key2 = None
                    elif key2 == "play":
                        client_sock.send("play")
                        key2 = None
                    elif key2 == "quit":
                        client_sock.send("quit")
                        key2 = None
                    elif key2 == "next":
                        client_sock.send("next")
                        key2 = None
                    elif key2 == "previous":
                        client_sock.send("previous")
                        key2 = None
                    elif key2 == "base":
                        client_sock.send("base")
                        key2 = None
                    elif key2 == "dir":
                        client_sock.send("dir")
                        key2 = None
                except KeyboardInterrupt:
                    print("disconnected")
                    break

            client_sock.close()
            server_sock.close()
    return

@app.route("/data", methods=['GET','POST'])
def getData():
    global key1
    key1 = request.args.get('key')
    print(key1)
    return "OK"

if __name__ == '__main__':
    b1_tread = threading.Thread(target=blue1)
   # b2_tread = threading.Thread(target=blue2)
    b1_tread.start()
   # b2_tread.start()
    app.run(host='0.0.0.0', port=9123)
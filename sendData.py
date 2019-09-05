from bluetooth import *


server_sock=BluetoothSocket(RFCOMM )

port = 1
server_sock.bind(("",port))
server_sock.listen(1)

client_sock,address = server_sock.accept()
print("Accepted connection from ",address)

while True:
    key = input()
    try:
        if key == 1:
            client_sock.send("open")
        elif key == "pause":
            client_sock.send("pause")
        elif key == "play":
            client_sock.send("play")
        elif key == "quit":
            client_sock.send("quit")
        elif key == "next":
            client_sock.send("next")
        elif key == "previous":
            client_sock.send("previous")
        elif key == "base":
            client_sock.send("base")
        elif key == "dir":
            client_sock.send("dir")
    except KeyboardInterrupt:
        print("disconnected")
        break

client_sock.close()
server_sock.close()
from bluetooth import *
from omxplayer.player import OMXPlayer
from pathlib import Path
from time import sleep
import socket
import sys
import errno

#######################################################
# Connect
#######################################################

# establishing a bluetooth connection
target_address = "B8:27:EB:C0:82:E4"
port = 1
while True:
    try:
        sock=BluetoothSocket( RFCOMM )
        sock.connect((target_address, port))
        hsock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_address = ('localhost', 8282)
        hsock.connect(server_address)
        VIDEO_PATH = Path("video/joy.mp4")
        VIDEO_PATH2 = Path("video/yuju.mp4")
        key = None
        ret = None
        frame = None
        player = None
        while True:         
            try:
                key = sock.recv(1024)
                key = key.decode('ascii')
                print(key)
                if key == "open":
                    if player==None:
                        player = OMXPlayer(VIDEO_PATH)
                        #player.show_video()
                    else:
                        print(key)
                        player.quit()
                        player = OMXPlayer(VIDEO_PATH2) 
                elif key == "play":
                    print(key)
                    player.play()
                elif key == "quit":
                    print(key)
                    player.quit()
                elif key == "pause":
                    print(key)
                    player.pause()
                elif key == "next":
                    print(key)
                    player.seek(5.0)
                elif key == "previous":
                    print(key)
                    player.seek(-5.0)
                elif key == "base":
                    print(key)
                    hsock.send(b'base')
                elif key == "dir":
                    print(key)
                    hsock.send(b'dir')
            except KeyboardInterrupt:
                print("disconnected")
                sock.close()
                hsock.close()
                print("all done")
                sleep(5)
                player.quit()
    except btcommon.BluetoothError as err:
        print('An error occurred : %s ' % err)
        sleep(1)
        pass
    except socket.error as serr:
        if serr.errno != errno.ECONNREFUSED:
            raise serr
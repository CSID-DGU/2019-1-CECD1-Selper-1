from time import sleep
import cv2
import numpy as np
import socket
import threading

mode = 0

def background():
    global mode
    t_count = 0
    bg = np.zeros((989,618))
    dr = np.zeros((989,618,3))
    guide_bar = cv2.imread('video/guide_bar.png')
    tt = cv2.imread('video/thumbnail/joy0.png')
    dr[:89, :] = guide_bar[:,:]
    t_title = cv2.imread('video/joy_title.png')
    dr[439:539, :] = t_title[:,:]
    t_title2 = cv2.imread('video/yooju_title.png')
    dr[889:989, :] = t_title2[:,:]
    while True:
        if mode == 0:
            cv2.imshow('BackGround', bg)
            cv2.waitKey(250)
        elif mode == 1:
            t_count %= 30
           # print(t_count)
            thumbnail = cv2.imread('video/thumbnail/joy'+str(t_count%30)+'.png')
            length = thumbnail.shape[1]
            if length < 618:
                dr[89:439, int((618-length)/2):int((618+length)/2)] = thumbnail[:,:]/255
            else:
                dr[89:439, :] = thumbnail[:,:]/255
            thumbnail = cv2.imread('video/thumbnail/yooju'+str(t_count%30)+'.png')
            length = thumbnail.shape[1]
            dr[539:889, int((618-length)/2):int((618+length)/2)] = thumbnail[:,:]/255
            cv2.imshow('BackGround', dr)
            t_count += 1
            cv2.waitKey(100)
    return
if __name__ == '__main__':
    mode = 0
    cv2.namedWindow('BackGround', cv2.WND_PROP_FULLSCREEN)
    cv2.setWindowProperty('BackGround', cv2.WND_PROP_FULLSCREEN, cv2.WINDOW_FULLSCREEN)
    bg_thread = threading.Thread(target=background)
    bg_thread.start()
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', 8282))
    server_socket.listen(1)
    
    print("1234")
    connection, client_address = server_socket.accept()
    while True:
        try:
            key = connection.recv(1024)
            if key:
                key = key.decode('ascii')
                if key == 'base':
                    mode = 0
                elif key == 'dir':
                    mode = 1
        finally:
            pass
    server_socket.close()
    connection.close()
    cv2.destroyAllWindows()
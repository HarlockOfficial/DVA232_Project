from threading import Thread
import requests
from string import ascii_letters, digits
import time
import json
import random

from Pinger import Pinger


class TicTacToeBot(Thread):
    def __init__(self):
        super().__init__()
        self.__uuid = "bot_"+"".join(random.choices(ascii_letters + digits + "_", k=14))
       # self.__url = "http://dva232-project-group-7.atwebpages.com/?game=ttt&player=" + self.__uuid
        self.__url = "http://localhost/?game=ttt&player=" + self.__uuid
        self.__field = []
        self.__sign = None
        self.__pinger = Pinger("ttt", self.__uuid)

    def run(self) -> None:
        r = requests.get(self.__url + "&action=add_queue")
        self.__pinger.start()
        if r.json()['response'] == "in_queue":
            print("Tic Tac Toe Bot In Queue")
            while True:
                r = requests.get(self.__url + "&action=get_queue")
                if r is not None and r.json()['response'] != "in_queue":
                    break
                time.sleep(0.01)
        print("Tic Tac Toe Bot playing")
        ret = json.loads(r.json()['response'])
        self.__sign = 'x'
        self.__field = ret['field'].split(",")
        starting_player = ret['starting_player']
        if starting_player == self.__uuid:
            self.__sign = 'o'
        print(self.__sign)
        if self.__sign == 'o':
            self.__set_move()
            return
        self.__get_move()

    def __set_move(self):
        # update field
        r = requests.get(self.__url + "&action=get_move")
        r = json.loads(r.json()['response'])
        self.__field = r['field'].split(",")
        # ------------------------------------------------
        while True:
            x = random.randint(1, 9)
            if self.__field[x-1] == "-":
                self.__field[x-1] = self.__sign
                break
        r = requests.get(self.__url + "&action=add_move&move=" + str(x) + "," + self.__sign)
        if r.json()['response'] != "ok":
            print(r.text)
            exit(-1)
        # update field
        r = requests.get(self.__url + "&action=get_move")
        r = json.loads(r.json()['response'])
        self.__field = r['field'].split(",")
        # -------------------------------------------------
        print(x, self.__field)
        self.__get_move()

    def __get_move(self):
        changed = False
        while not changed:
            r = requests.get(self.__url + "&action=get_move")
            r = json.loads(r.json()['response'])
            tmp = r['field'].split(",")
            if r['winner'] != "":
                self.__pinger.stop()
                if r['winner'] == "draw":
                    print("draw")
                    exit(0)
                if r['winner'] == self.__sign:
                    print("win")
                    exit(0)
                print("lose")
                exit(0)
            for i in range(len(self.__field)):
                if tmp[i] != self.__field[i]:
                    self.__field = tmp
                    changed = True
                    break
        self.__set_move()

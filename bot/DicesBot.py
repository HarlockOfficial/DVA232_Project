from threading import Thread
import requests
from string import ascii_letters, digits
import time
import random

from Pinger import Pinger


class DicesBot(Thread):
    def __init__(self, num_of_dices: int):
        super(DicesBot, self).__init__()
        self.__dices_number = num_of_dices
        self.__uuid = "bot_"+"".join(random.choices(ascii_letters + digits + "_", k=14))
        self.__url = "http://dva232-project-group-7.atwebpages.com/?game=dices&player=" + self.__uuid
        self.__pinger = Pinger("dices", self.__uuid)

    def run(self) -> None:
        r = requests.get(self.__url + "&action=add_queue")
        self.__pinger.start()
        if r.json()['response'] == "in_queue":
            print("Dices Bot In Queue")
            while True:
                r = requests.get(self.__url + "&action=get_queue")
                if r is not None and r.json()['response'] != "in_queue":
                    break
                time.sleep(0.01)
        print("Dices Bot playing")
        self.__set_move()

    def __set_move(self):
        r = requests.get(self.__url + "&action=add_move&move=" + str(self.__dices_number))
        try:
            my_dices = int(r.json()['response'])
        except ValueError:
            print(r.text)
            exit(-1)
        self.__get_move(my_dices)

    def __get_move(self, my_result: int):
        while True:
            r = requests.get(self.__url + "&action=get_move")
            if r.json()['response'] is not None:
                break
        try:
            opponent_result = int(r.json()['response'])
        except ValueError:
            print("impossible error")
            exit(-1)
        print("My result:", my_result, "Opponent result:", opponent_result)
        if my_result > opponent_result:
            print("win")
        elif my_result == opponent_result:
            print("draw")
        else:
            print("lose")
        self.__pinger.stop()

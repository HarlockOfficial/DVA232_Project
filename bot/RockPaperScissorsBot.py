from threading import Thread
import requests
from string import ascii_letters, digits
import time
import random
from enum import Enum


class Possibilities(Enum):
    rock = 1
    paper = 2
    scissors = 3


class RockPaperScissorsBot(Thread):
    def __init__(self):
        super().__init__()
        self.__uuid = "".join(random.choices(ascii_letters + digits + "_", k=20))
        self.__url = "https://dva232-project-group-7.000webhostapp.com/?game=rps&player=" + self.__uuid

    def run(self) -> None:
        r = requests.get(self.__url + "&action=add_queue")
        if r.json()['response'] == "in_queue":
            print("Rock Paper Scissors Bot In Queue")
            while True:
                r = requests.get(self.__url + "&action=get_queue")
                if r is not None and r.json()['response'] != "in_queue":
                    break
                time.sleep(0.01)
        print("Rock Paper Scissors Bot playing")
        self.__set_move()

    def __get_move(self, my_choice: Possibilities):
        while True:
            r = requests.get(self.__url + "&action=get_move")
            if r.json()['response'] is not None:
                break
        opponent_choice = Possibilities[r.json()['response']]
        print("I played:", my_choice.name, "Opponent played:", opponent_choice.name)
        if opponent_choice.value % 3 + 1 == my_choice.value:
            print("win")
        elif my_choice.value % 3 + 1 == opponent_choice.value:
            print("lose")
        else:
            print("draw")

    def __set_move(self):
        choice = Possibilities(random.randint(1, 3))
        r = requests.get(self.__url + "&action=add_move&move=" + choice.name)
        if r.json()['response'] != "ok":
            print(r.text)
            exit(-1)
        self.__get_move(choice)

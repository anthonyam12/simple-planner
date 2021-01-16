import datetime
import json
import os 
import sys

def print_tasks(task_list):
    if len(task_list) == 0:
        return

    print("\tTitle\t|\tDescription\t|\tDue Date\t")
    print("--------------------------------------------------------------")

    for task in task_list:
        title = task["title"] if len(task["title"]) < 15 else task["title"][:15]
        description = task["description"] if len(task["description"]) < 21 else task["description"][:21]
        due_date = task["due_date"]

        print("{} | {} | {}".format(title, description, due_date))
    print("\n")

def get_task_id(task_list):
    if len(task_list) == 0:
        return

    print("Select a task ID: ")

    valid_ids = []
    for task in task_list:
        valid_ids.append(task["id"])
        title = task["title"] if len(task["title"]) < 15 else task["title"][:15]
        description = task["description"] if len(task["description"]) < 25 else task["description"][:25]
        print("\tid: {}\ttitle: {}\tdescription: {}".format(task["id"], title, description))

    selection = input("\t>>")
    while int(selection) not in valid_ids:
        print("\tInvalid selection.")
        selection = input("\t>>")

    return int(selection)

def print_task_details(task_list, task_id):
    print("")
    for task in task_list:
        if task["id"] == task_id:
            print("TITLE:")
            print("\t{}\n".format(task["title"]))
            print("DESCRIPTION:")
            print("\t{}\n".format(task["description"]))
            print("DUE DATE:")
            print("\t{}\n".format(task["due_date"]))
    print("")

def print_menu():
    print("1. Add Event")
    print("2. Event Details")
    print("3. Modify Event")
    print("4. Delete Event")
    print("5. Exit")
    print("")

    choice = input("> ")
    if choice == "":
        return 0
    try:
        choice = int(choice)
    except ValueError:
        return 20000000

    print("")

    return int(choice) 

def valid_choice(choice):
    return choice in [1, 2, 3, 4, 5]

def get_next_id(task_list):
    next_id = 0
    for task in task_list:
        if task["id"] > next_id:
            next_id = task["id"]
    return next_id + 1

def get_task_data(task_list):
    data_dict = {}

    data_dict["title"] = input("\tTitle: ")
    data_dict["description"] = input("\tDescription: ")
    data_dict["due_date"] = input("\tDue Date: ")
    data_dict["id"] = get_next_id(task_list)

    print("")

    return data_dict

def delete_task(task_list, task_id):
    idx = 0
    for i in range(len(task_list)):
        task = task_list[i]
        if task["id"] == task_id:
            idx = i
            break
    del task_list[idx]

def modify_task(task_list, task_id):
    for task in task_list:
        if task["id"] == task_id:
            task["title"] = input("\tTitle [{}]: ".format(task["title"])) or task["title"]
            task["description"] = input("\tDescription [{}]: ".format(task["description"])) or task["description"]
            task["due_date"] = input("\tDue Date [{}]: ".format(task["due_date"])) or task["due_date"]
            break
    print("")

if __name__ == '__main__':
    print("Welcome to Simple Planner CLI\n")
    filename = "tasks.json"

    task_dict = {}
    task_list = []
    if os.path.isfile(filename):
        with open(filename, "r") as jfile:
            task_dict = json.load(jfile)
            task_list = task_dict["tasks"]

    while True:
        print_tasks(task_list)
        choice = print_menu()

        if choice == 5:
            break
        elif choice == 1:
            task_list.append(get_task_data(task_list))
        elif choice == 2:
            selected_id = get_task_id(task_list)
            print_task_details(task_list, selected_id)
        elif choice == 3:
            selected_id = get_task_id(task_list)
            modify_task(task_list, selected_id)
        elif choice == 4:
            selected_id = get_task_id(task_list)
            delete_task(task_list, selected_id)
        elif choice == 0:
            pass
        else:
            print("\nInvalide menu option.\n")

    task_dict["tasks"] = task_list
    with open(filename, "w") as tasks_file:
        json.dump(task_dict, tasks_file)
    print("Exiting...")    

# -*- coding: utf-8 -*-

import os.path
import io
import json
import argparse


# Argparse
parser = argparse.ArgumentParser(
    description="Transform a saiku log file into a json file"
    )
parser.add_argument(
    "infile",
    help="saiku log filepath to transform in json"
    )
parser.add_argument(
    "outfile",
    nargs="?",
    default="saiku_log.json",
    help="output filepath"
    )
args = parser.parse_args()


# Check output file
if os.path.isfile(args.outfile):
    print("File {} already exist. Do you want to overwrite it ? (y/n)".format(
        args.outfile
        ))
    answer = input("> ").strip().lower()
    if answer == "n":
        exit()
    elif answer != "y":
        print("Unknow answer, aborting.")
        exit()


# Read log
with open(args.infile, "r", encoding="utf8") as infile:

    queries = []
    state = "looking"  # looking, query, duration

    for line in infile:
        # Test state
        if state == "looking":
            line = line.strip()
            if line.find("select") != -1:
                query = line
                state = "query"
            else:
                continue
        elif state == "query":
            if line != "\n":
                query += " " + line.strip()
            else:
                state = "duration"
        elif state == "duration":
            duration = line.strip()

            # Then store query
            datetime = query[:23]  # query datetime
            query = query[45:]
            id = int(query[: query.find(":")])  # Query id
            query = query[query.find(":") + 2:]  # Query text
            duration = int(duration[  # Query duration
                duration.rfind(":") + 2:duration.rfind("ms") - 1
                ])

            # Build dictionary
            query = {
                "id": id,
                "query": query,
                "datetime": datetime,
                "duration": duration,
                }
            queries.append(query)
            state = "looking"  # Reset line state

# Write json file
with io.open(args.outfile, "w", encoding="utf-8") as outfile:
    json_obj = {
        "user": {"id": None, "name": None, "group": None},
        "cube": None,
        "title": None,
        "comment": None,
        "queries": queries
        }
    json.dump(json_obj, outfile, indent=4, ensure_ascii=False)

print("{} successfully created".format(args.outfile))

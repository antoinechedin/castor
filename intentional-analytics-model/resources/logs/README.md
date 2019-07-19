# Logs

Intentional Analytics model work with list of OLAP request. These requests often
comes from user exploration session on a data cube.

The majority of our data comes from Saiku log, which aren't very practical. To
use them, you can use the `transform_log_to_json.py` script to translate logs
file in json.

### Usage

```
usage: log_to_json_script.py [-h] infile [outfile]

Transform a saiku log file into a json file

positional arguments:
  infile      saiku log filepath to transform in json
  outfile     output filepath

optional arguments:
  -h, --help  show this help message and exit
```

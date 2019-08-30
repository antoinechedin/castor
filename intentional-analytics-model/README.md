# Intentional Analytics Model

### Quick Start

Look at [`scripts.BuildDemoJsonScript`](src/main/java/scripts/BuildDemoJsonScript.java) 
and [`scripts.Playground`](src/main/java/scripts/Playground.java) in 
[`src/main/java/scripts/`](src/main/java/scripts/) for usage example about 
database connection and Algorithm 1 initialisation.

### Dependencies

The project is managed by maven for most of the dependencies. Still, you need to
manually import the Mondrian jars in [`lib/mondrian/`](lib/mondrian/) in order 
to have access to the database drivers.
 
I've searched for this jars on the Maven repository but it seems that since
Mondrian was acquired by Hitachi, the dependencies are now private.
   
### Packages

Some documentation about the different java package in the project.

- **algorithm:** Algorithm 1 implementation .
    - **builders:** Regroup all implementations of proxies, significance,
	surprise, component and model score function.
    - **models:** Selection models for highlighting cells.
- **castor:** Code relative to *Castor* client communication.
    - **response:** Json input model of the *Castor* client.
    - **session:** Json model of a user OLAP exploration session.
- **olap:** OLAP data model. Olap4J data model isn't very friendly, that's why
we rebuilt a data model above it.
- **scripts:** All executable class.
- **utils:** A collection of class with useful method for the project.  

### UML models

There's small UML models  in [`uml/`](uml/) of how OLAP structure is represented in *olap4j* and
how cator use it.

### Saiku logs

To load Saiku logs as input for the intentional analytics model, you can use the
`transform_log_to_json.py` script to translate logs in json. The script is
located in [`resources/logs/`](resources/logs/).

commandeer
==========

Easily define and parse text commands of format commonly used by IRC bots.

Getting started
---------------


 
```Java
//define the format of the command
private static final String COMMAND = "command|cmd <arg1:text> <arg2:int> [arg3:real] [arg4:time]";

//use builder or simply new Commandeer() to use defaults
Commandeer cmd = new CommandeerBuilder()
        .setPrefix("!") //default input prefix
        .addType("time", input -> { // adding new type 'time'
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            try {
                return sdf.parse(input);
            } catch (ParseException e) {
                throw new RuntimeException(e.toString());
            }
        })
        .create();

//register commands
cmd.addCommand(COMMAND);
```

The command format was defined as `command|cmd <arg1:text> <arg2:int> [arg3:real] [arg4:time]`.

The first part defines the command aliases, either a single alphanumeric word
or multiple separated with pipe characters. What follows is the arguments,
the first one is named `arg1`, it's required and can be anything at all, the
second takes a whole number. The third takes a double while the fourth takes
HH:mm:ss (defined in the snippet below), both of which are optional.

```Java
public void process(String input) {
    CommandParser parser = cmd.parse(input);

    System.out.println("input: " + input);

    if(parser == null) {
        System.out.println("\tnot a command");
        return;
    }

    String error;
    if ((error = parser.getError()) != null) {
        //tells us why the input was not valid
        System.out.println("\terror: " + error);
    } else {
        //get instance of the parsed user input
        Command command = parser.getCommand();

        //check out what's what
        System.out.println("\tresult: " + command.toString());

        // the specific alias that was used
        String alias = command.getAlias();
        //a map of the argument names and typed values
        Map<String, Object> args = command.getArgs();

        //argument values by name
        String arg1 = command.getArgAsString("arg1");

        if (command.hasArg("arg2")) {
            int arg2 = command.getArgAsInteger("arg2");
        }

        if (command.hasArg("arg3")) {
            double arg3 = command.getArgAsDouble("arg3");
        }

        if (command.hasArg("arg4")) {
            Date arg4 = (Date) command.getArg("arg4");
        }
    }
}
```

Examples
--------

The sort of responses you can expect from valid and less valid inputs.

```
format: command|cmd <arg1:text> <arg2:int> [arg3:real] [arg4:time]
input: command
	not a command
input: !command red green
	error: 'green' is not a valid argument value for arg2:int (java.lang.NumberFormatException: For input string: "green")
input: !command "red green"
	error: Argument <arg2:int> is not optional.
input: !cmd string word
	error: 'word' is not a valid argument value for arg2:int (java.lang.NumberFormatException: For input string: "word")
input: !cmd string 3.141
	error: '3.141' is not a valid argument value for arg2:int (java.lang.NumberFormatException: For input string: "3.141")
input: !cmd string 42
	result: Command{alias='cmd', args={arg1 (String)=string, arg2 (Integer)=42}}
input: !cmd string 42 3.141
	result: Command{alias='cmd', args={arg1 (String)=string, arg2 (Integer)=42, arg3 (Double)=3.141}}
input: !cmd string 42 3.141 water
	error: 'water' is not a valid argument value for arg4:time (java.lang.RuntimeException: java.text.ParseException: Unparseable date: "water")
input: !cmd string 42 3.141 22:52:11
	result: Command{alias='cmd', args={arg1 (String)=string, arg2 (Integer)=42, arg3 (Double)=3.141, arg4 (Date)=Thu Jan 01 22:52:11 GMT 1970}}
```
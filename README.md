commandeer
==========

Easily and safely define and query text commands of a format commonly used by IRC bots.

Getting started
---------------

The command format is given as `command|cmd <arg1:text> <arg2:int> [arg3:real] [arg4:time]`.

The first part defines the command aliases, either a single alphanumeric word
or multiple separated with pipe characters. What follows is the arguments, written
as the argument name and type separated by a colon. Required arguments
are enclosed in angle brackets while optional ones in square brackets.

In the above example we also add a type that is not handled by default, `time` along with
a method to convert text input into a relevant Java object. If it cannot be done (or if a
checked exception is thrown), we'll throw a descriptive unchecked exception.
 
Finally we register our test command.
 
```Java
//define the format of the command
final String COMMAND =
        "command|cmd <arg1:text> <arg2:int> [arg3:real] [arg4:time]";

//construct a new commandeer instance
Commandeer cmd = new Commandeer.Factory()
        .setDefaultPrefix("!") //default input prefix
        .addArgType("time", input -> { // adding new type 'time'
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            try {
                return sdf.parse(input);
            } catch (ParseException e) {
                throw new RuntimeException(e.toString());
            }
        })
        .setOnError((input, error) -> //will be called when given bad input
                System.out.println("\terror (callback): " + error + ", input: " + input))
        .create();

//register command with callback
cmd.registerCommand(CMD, (cmdIn) ->
    System.out.println("\tcmdIn (callback): " + cmdIn));
//or one without
cmd.registerCommand(CMD2);
```

At this point we can feed user input straight into Commandeer.

```Java
final String[] input = {
        "!cmd red",
        "!cmd2 red 42 3.141 22:52:11"};
final String[] badInput = {
        "!cmd red black",
        "!cmd2 red 42 3.141 water"};

//simply call execute to parse and execute appropriate callback if possible
//otherwise Commandeer.onError will be called with what went wrong
cmd.execute(input[0]);
cmd.execute(badInput[0]);

//or process command input and any errors yourself (throws CommandInputException)
processInput(input[1]);
processInput(badInput[1]);
```

```Java
public void processInput(String input) {
    try {
        CommandInput cmdIn = cmd.parse(input);

        //check out what's what
        System.out.println("\tcmdIn: " + cmdIn.toString());

        // the specific alias that was used
        String alias = cmdIn.getAlias();
        //a map of the argument names and typed values
        Map<String, Object> args = cmdIn.getArgs();

        //argument values by name
        String arg1 = cmdIn.getArgAsString("arg1");
        if (cmdIn.hasArg("arg2"))
            cmdIn.getArgAsInteger("arg2");

    } catch (CommandInputException e) {
        System.out.println("\terror: " + e.getMessage() + ", input: " + input);
    }
}
```

The result of the above inputs would give
```
cmdIn (callback): Command{alias='cmd', args={arg1 (String)=red}}
error (callback): 'black' is not a valid argument value for arg2:int (java.lang.NumberFormatException: For input string: "black") for input: !cmd red black
cmdIn: Command{alias='cmd2', args={arg1 (String)=red, arg2 (Integer)=42, arg3 (Double)=3.141, arg4 (Date)=Thu Jan 01 22:52:11 GMT 1970}}
error: 'water' is not a valid argument value for arg4:time (java.lang.RuntimeException: Unparseable date: "water") for input: !cmd2 red 42 3.141 water
```
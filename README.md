commandeer
==========

text command parsing made easy and safe

Getting started
---------------

```Java
    //define the format of the command
    public static final String CMD = "command|cmd <arg1:text> [arg2:int] [arg3:real]";

    //create a command definition using a builder
    CommandBuilder builder = new CommandBuilder();
    CommandDef cmdDef = builder.defineCommand("command|cmd <arg1:text> [arg2:int] [arg3:real]");

    public void process(String input) {
        CommandParser parser = new CommandParser(cmdDef, input, "!");
        parser.parseCommand();

        String error;
        if((error = parser.getError())!=null) {
            //tells us why the command was not called properly
            System.out.println("Error: " + error);
        } else {
            //get instance of the parsed user input
            Command command = parser.getCommand();

            // the specific alias that was used
            String alias = command.getAlias();
            //a map of the argument values, addressed by argument name
            Map<String,Object> args = command.getArgs();
        }
    }
```

Examples
--------

The sort of results you can expect from various inputs.

```
format: command|cmd <arg1:text> [arg2:int] [arg3:real]

input: !command some string
	error: Cannot accept 'string' as value for argument arg2:int
input: !command "some string"
	result: Command{alias='command', args={String arg1=some string}}
input: !cmd string word
	error: Cannot accept 'word' as value for argument arg2:int
input: !cmd string 10
	result: Command{alias='cmd', args={Integer arg2=10, String arg1=string}}
input: !cmd string 5.4 10
	error: Cannot accept '5.4' as value for argument arg2:int
input: !cmd string 10 4.4
	result: Command{alias='cmd', args={Double arg3=4.4, Integer arg2=10, String arg1=string}}
```
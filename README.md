commandeer
==========

Easily and safely define and query text commands of a format commonly used by IRC bots.

Getting started
---------------

When working with commandeer you'll first need to define commands. There are two ways
to do this - annotating methods and writing format strings.

### Commands through annotated methods

The below command demonstrates the former. Simply annotate the method with @Cmd and
pass a list of 'aliases' into it. An alias is just a word by which a command can be
addressed.

```Java
@Cmd({"command", "cmd"})
public void doCommand(String arg1, Integer arg2,
                      @Param(name = "three", def = "4.11") Double arg3,
                      @Param(name = "four", def = "12:11:30") Date arg4) {
    System.out.println("\tcmd: " + arg1 + ", " + arg2 + ", " + arg3 + ", " + arg4);
}
```

Although optional, @Param can be used to give a parameter a name so that meaningful
command usage information can be generated. It can also be used to mark parameters
as optional by defining their default string value.

### Commands through format string

Alternatively you can define your command with a string that specifies its format.
Below is a command written to suit the default CommandParser implementation
although it can be swapped out with one written to handle a different format.

```
command|cmd <arg1:text> <arg2:int> [arg3:double] [arg4:time]
```

The first part defines the command aliases, either a single alphanumeric word
or multiple separated by pipe characters. What follows is the parameters,
expressed as their name and type separated by a colon. Required arguments
are enclosed in angle brackets while optional ones in square brackets.

### Register and execute

Now you can start running text commands. First construct a Commandeer instance
as per your needs. Then you can pass an object to `extractCommands` to have it
scanned for annotated methods.

```Java
cmd = new Commandeer.Builder()
        .setDefaultPrefix("!") //default input prefix
        .setOnError((def, input, error) -> //will be called when given bad input
                System.out.println("error (callback): " + error + ", input: " + input))
        .create();

//add commands from annotations
cmd.extractCommands(this);
//add format string command and an associated callback
cmd.defineCommand("command|cmd <arg1:text> <arg2:int> [arg3:real] [arg4:time]", (cmdIn)
        -> System.out.println("cmdIn (callback): " + cmdIn));
        
//process input
cmd.execute(input);
```

Finally you can pass text command input into cmd.execute. Given all the required arguments
are present and have been successfully parsed into their relevant types the callback
methods will be executed.

### Parameter types

You must define your own types and parsers to handle
any types beyond the stock ones - text, int and double.

Below is a Commandeer instance built to handle a new type - `time`.
It specifies the Java object it maps to and gives instructions on
how to convert string input to this class.
```Java
cmd = new Commandeer.Builder()
        .addType("time", new TypeParser<Date>(Date.class) {
            @Override
            public Date parse(String input) {// adding new type 'time'
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                try {
                    return sdf.parse(input);
                } catch (ParseException e) {
                    throw new RuntimeException(e.toString());
                }
            }
        })
        .create();
```

### More

Check out [`AnnotatedSample`](https://github.com/Pucilowski/commandeer/blob/master/src/main/java/com/pucilowski/commandeer/samples/AnnotatedSample.java) 
to see annotated methods, or [`ClassicSample`](https://github.com/Pucilowski/commandeer/blob/master/src/main/java/com/pucilowski/commandeer/samples/ClassicSample.java)
to see the original format strings in action.
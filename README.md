# Markdown => HTML converter

This is a Scala & Play Server Application that extends a POST endpoint for submitting
requests to convert a String input of Markdown language to a limited set of HTML tags.

It implements the conversion itself using an Abstract Syntax Tree, implemented with the
use of Scala Parser Combinators. For the AST we defined 3 types of Nodes: A header node, a paragraph node
and a link node.

Generally the service works by receiving a POST request with a json body payload that looks like:

`{"markdown": "## Sample Document\n\nHello!\n\nThis is sample markdown for the [Mailchimp](https://www.mailchimp.com) homework assignment.\n"}`

The service returns a json body payload with a field representing the conversion to HTML:

`{"rawHtml": "\<h2>Sample Document\</h2>\<p>Hello!\</p>\<p>This is sample markdown for the \<a href=https://www.mailchimp.com>Mailchimp\</a> homework assignment.\</p>"}`
(using escape characters here for the readme md)

## How to Compile & Run

This project requires SBT. If you dont have SBT installed, and you are on a mac, it is recommended to use sdkman to download and install rather
than Homebrew. The reason being is that Homebrew packages and forces its own installation of JDK onto its installation of SBT. and that version of 
Java is not compatible with Play atm.

SBT version: latest
Java version: Java 11 (17 is marked as experimental with Play, and later versions are not yet supported)

Once SBT is ready, and you have navogated to this projects home directory, you can compile the code using:
`sbt compile`

and then finally run it with:
`sbt run`

This launches the server application, listening on localhost:9000

the post endpoint then can be reached by curling localhost:9000/postMethod

example:
`curl --header "Content-Type: application/json" --request POST --data '{"markdown": "## Sample Document\n\nHello!\n\nThis is sample markdown for the [Mailchimp](https://www.mailchimp.com) homework assignment.\n"}' http://localhost:9000/postMethod | jq`

## Unit tests

The MarkdownParser has some unit tests for sanity checking. Thes can be run using:
`sbt test`

## Assumptions, Considerations and Discussion

With the limited information given, and no ability to dig into the requirements further, some assumptions were made:

1) Initially i implemented the links as standalone items. I.e. not detectable inside the bodies of other Nodes...But based from the examples
   seems the opposite was true. So the assumption was made that Links exist in the context of other bodies of text. and even a line with only a link,
   is essentially a paragraph with only a link item in it, unless it is explicitly a header
2) I couldve made my life a bit easier and implemented the converter with a map, and a recursive call to detect patterns (links) inside patterns (a paragraph). However,
   after a bit of research, all production grade markdown->HTML libraries tend to implement this sort of thing using some sort of custom Abstract Syntax Tree implementation.
   I was convinced the AST was the proper way to do it, and went for it.

The AST approach adds a bit of boiler-plate code in order to define the structure of your AST Nodes, which could be more tasking to maintain. However, expanding on the language
by adding node types makes life easier to build out a more extensive language parser.

I have taken the appraoch of building it as a server application and a microservice. Mainly because most things are these days, and we generally are in the business of building server applications
that communicate with each other and the public.


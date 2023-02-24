# fluxtail

Largely written while learinng Java Reactor, I thought it would be nice to put a tailer onto a flux.

## API

* new Tail(..).read() - returns Flux<Character> with each char emitted from the given path
* new Tail(..).read(Supplier<CharBuffer> bufferFactory) - returns Flux<CharSequence> with strings emitted, as split by the provided CharBuffer
* new Tail(..).read(Supplier<CharBuffer> bufferFactory, Parser<T> parser) - returns Flux<T> of objects parsed from the CharBuffer stream

## Offerings

* CharFluxTail - emits every character on the flux
* StringFluxTail - emits strings after splitting the char stream
* FluxTail - emits objects parsed from StringFluxTail emissions


## Concepts

### CharBuffer 
Collects and enables splitting of chars into chunks (e.g., lines), used in StringFluxTail and FluxTail.

### Parser 
Parses char chunks into objects, used in FluxTail.

### TailHandler
Accepts chars and errors and emits onto flux (see CharFluxTail, StringFluxTail, and FluxTail).

### RABReader
The RABReader combines Java's RandomAccessFile and BufferedReader, to allow random access buffered file reading.
It also offers methods for assessing if the file has been moved, e.g., in the case of log rotations.

### TailReader 
TailReader uses RABReader to tail a file while handling renamings, submitting each char to a given TailHandler.
The given file is tailed (and chars submitted to handler) on a new thread upon calling `start()`. 
Tailing is stopped via `stop()`.


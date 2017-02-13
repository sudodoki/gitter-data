# Notes

# gitter-data

This is a CLI tool to dump selected REPO related rooms messages in Gitter you are part off. Gitter API limits messages to 5000 most recent per room.

![](http://g.recordit.co/PumFa6fB2H.gif)
or fancy [video](https://www.youtube.com/watch?v=NkvmQLKfPZo)

## Installation

You need to have [Leiningen](https://leiningen.org/) installed.
```
  git clone git@github.com:sudodoki/gitter-data.git gitter-data
```

## Usage

You will need to pass `gitter-token` via environment variable. You can find one under [Personal Access Token](https://developer.gitter.im/apps) section. You have 2 options to pass it to the app:
  1. copy `profiles.clj.example` to `profiles.clj` and put a proper gitter token in
  2. pass it from command line. For me it would be something like `env gitter-token=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX lein run`
  
To run app just execute `lein run`


## Options

No options.

## License

Copyright © 2017 sudodoki

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

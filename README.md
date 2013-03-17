# clj-scribe

A Clojure client for the Scribe log server.

## Installation

Add the following to your lein dependencies:

    [livingsocial/clj-scribe "0.2.0"]

## Usage

    (require '[clj-scribe :as scribe])
  
    (scribe/setup :host "localhost" :port 1463 :category "my-app")

By default, errors communicating with Scribe are written to standard error. You can
override the error handling with your own function that takes the category,
messages and the Exception encountered.

    (scribe/setup :host "localhost"
                  :port 1463
                  :category "my-app"
                  :error-handler (fn [category messages exception]
                                   (println "I'm good at handling errors!")))

The log function takes a sequence of message Strings:

    (scribe/log ["This is a message."
                 "This is some other message."
                 "Etc."])

## License

This software is released under the MIT license:

Copyright (C) 2013, LivingSocial, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

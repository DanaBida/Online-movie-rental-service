

#include "../include/keyboardHandler.h"


keyboardHandler::keyboardHandler(ConnectionHandler & _handler) : handler(_handler), buff_length(1024) {}

void keyboardHandler::run() {

    while (!handler.shouldTerminate())
    {
        char buf[buff_length];
        cin.getline(buf, buff_length);
        string line(buf);
		if(!handler.shouldTerminate())
			handler.sendLine(line);
    }
	handler.close();
}

keyboardHandler::~keyboardHandler() {}

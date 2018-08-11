/*
 * serverHandler.cpp
 *
 *  Created on: 7 ����� 2018
 *      Author: Admin
 */

#include "serverHandler.h"

serverHandler::serverHandler(ConnectionHandler & _handler) : handler(_handler) {}

void serverHandler::run() {

	while (!handler.shouldTerminate())
	{
        string answer;
        if(handler.getLine(answer)){
			handler.processAndPrint(answer);
		if(handler.shouldTerminate())
			handler.close();
        }
	}


}

serverHandler::~serverHandler() {}


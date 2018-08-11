/*
 * serverHandler.h
 *
 *  Created on: 7 ����� 2018
 *      Author: Admin
 */

#ifndef SERVERHANDLER_H_
#define SERVERHANDLER_H_

#include "../include/ConnectionHandler.h"


class serverHandler {
public:
	serverHandler(ConnectionHandler & _handler);
	virtual ~serverHandler();
	ConnectionHandler & handler;
	void run();
};

#endif /* SERVERHANDLER_H_ */

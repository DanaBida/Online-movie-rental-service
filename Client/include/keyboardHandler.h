/*
 * keyboardHandler.h
 *
 *  Created on: 7 ����� 2018
 *      Author: Admin
 */

#ifndef KEYBOARDHANDLER_H_
#define KEYBOARDHANDLER_H_

#include <iostream>
#include "../include/ConnectionHandler.h"
#include <boost/thread.hpp>
using namespace std;


class keyboardHandler {
public:
	keyboardHandler(ConnectionHandler & _handler);
	virtual ~keyboardHandler();
	void run();
	ConnectionHandler &handler;
	const short buff_length;
};

#endif /* KEYBOARDHANDLER_H_ */

/*
 * ConnectionHandler.h
 *
 *  Created on: 7 ����� 2018
 *      Author: Admin
 */

#ifndef CONNECTIONHANDLER_H_
#define CONNECTIONHANDLER_H_

#include <iostream>
#include <boost/asio.hpp>
#include <condition_variable>
#include <boost/thread.hpp>

using boost::asio::ip::tcp;
using namespace std;

class ConnectionHandler {


	bool isConnected;//change to true when the client get an ACK SIGNOUT message from the server.
	std::string host;
	short port;
	boost::asio::io_service io_service;
	tcp:: socket socket;

public:
	bool shouldTerminate();//returns: isConnected==false.
	void processAndPrint (string msg);
	bool connect();//connect to the server
	ConnectionHandler(string _host, short _port);
	virtual ~ConnectionHandler();
    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);

	// Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);

    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
    bool getLine(std::string& line);

	// Send an ascii line from the server
    // Returns false in case connection closed before all the data is sent.
    bool sendLine(std::string& line);

    // Get Ascii data from the server until the delimiter character
    // Returns false in case connection closed before null can be read.
    bool getFrameAscii(std::string& frame, char delimiter);

    // Send a message to the remote host.
    // Returns false in case connection is closed before all the data is sent.
    bool sendFrameAscii(const std::string& frame, char delimiter);
	void close();//close the server
};

#endif /* CONNECTIONHANDLER_H_ */

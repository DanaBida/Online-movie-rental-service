/*
 * ConnectionHandler.cpp
 *
 *  Created on: 7 ����� 2018
 *      Author: Admin
 */

#include "ConnectionHandler.h"


ConnectionHandler::ConnectionHandler(string _host, short _port) : isConnected(true), host(_host), port(_port),  io_service(), socket(io_service)
{}

bool ConnectionHandler::shouldTerminate(){
	return isConnected==false;
}

void ConnectionHandler:: processAndPrint(string msg)
{

	cout<< msg;
	//deletes the spaces in the string for compare using
	if(msg.find_first_of(" ")!=string::npos)
		msg.erase(msg.find_first_of(" "),1);
	if(msg.find_first_of(" ")!=string::npos)
		msg.erase(msg.find_first_of(" "),1);
	if(msg.find_first_of(" ")!=string::npos)
		msg.erase(msg.find_first_of(" "),1);
	if(msg.size()>=1)
		msg.erase(msg.size()-1,1);
	if (msg.compare("ACKsignoutsucceeded")==0)
	{
		cout<<"Ready to exit. Press enter"<<endl;
		isConnected = false;
	}
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
        << host << ":" << port << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host), port); // the server endpoint
		boost::system::error_code error;
		socket.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
//        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\n');
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
		do{
			getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}

void ConnectionHandler::close() {
    try {
        socket.close();
    } catch (...) {
        cout << "closing failed: connection already closed" << std::endl;
    }
}

ConnectionHandler::~ConnectionHandler() {}


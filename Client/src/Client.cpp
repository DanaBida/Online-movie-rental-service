
#include <stdlib.h>
#include <iostream>
#include <boost/thread.hpp>
#include "../include/ConnectionHandler.h"
#include "../include/keyboardHandler.h"
#include "../include/serverHandler.h"

using namespace std;

int main(int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler* handler = new ConnectionHandler(host,port);
    keyboardHandler keyboardHandler(*handler);
    serverHandler serverHandler(*handler);

       if (!handler->connect()) {
           cerr << "Cannot connect to " << host << ":" << port << endl;
           return 1;
       }

       boost::thread th1(&keyboardHandler::run, &keyboardHandler);
       boost::thread th2(&serverHandler::run, &serverHandler);
       th1.join();
       th2.join();

    return 0;
}

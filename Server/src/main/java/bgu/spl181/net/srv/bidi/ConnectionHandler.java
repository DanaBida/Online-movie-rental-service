/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.srv.bidi;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author bennyl
 */
public interface ConnectionHandler<T> extends Closeable{

	//send msg to the client of the conectionHandler
    void send(T msg) ;

}

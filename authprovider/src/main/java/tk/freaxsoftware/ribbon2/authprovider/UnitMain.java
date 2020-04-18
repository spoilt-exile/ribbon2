/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.freaxsoftware.ribbon2.authprovider;

import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.Receiver;
import tk.freaxsoftware.ribbon2.core.data.User;

/**
 *
 * @author spoilt
 */
public class UnitMain {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MessageBus.addSubscription(User.CALL_CHECK_AUTH, (Receiver) (MessageHolder message) -> {
            message.getResponse().setContent("ROOT");
        });
    }
}

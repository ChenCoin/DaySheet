/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daily.common;

import java.time.LocalDateTime;

/**
 *
 * @author tian.chen
 */
public class RawTick {
    public LocalDateTime time;
    public int inOrOut = -1;
    public String name = "";

    @Override
    public String toString() {
	String type;
	switch (inOrOut) {
	    case 0:
		type = "in";
		break;
	    case 1:
		type = "out";
		break;
	    default:
		type = "unknown";
	}
	return "time:" + time + " name:" + name + " " + type;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daily.common;

import java.util.List;

/**
 *
 * @author tian.chen
 */
public class RawDay {

    public int day;

    public List<RawTick> data;

    public RawDay(int day, List<RawTick> data) {
	this.day = day;
	this.data = data;
    }
}

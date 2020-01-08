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
public class RawPerson {
    public String name;

    public List<RawDay> data;

    public RawPerson(String name, List<RawDay> data) {
	this.name = name;
	this.data = data;
    }

    @Override
    public String toString() {
	return "name:" + name + " count:" + data.size();
    }
}

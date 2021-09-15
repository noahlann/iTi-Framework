package org.lan.iti.sdk.pay.util;

import lombok.experimental.UtilityClass;
import org.lan.iti.common.pay.constants.PayConstants;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author I'm
 * @since 2021/3/27
 * description pay sdk util 一些sdk的基本方法
 */
@UtilityClass
public class PaySdkUtils {

    public String getHeaderByName(ArrayList<String> list, String name){
        Optional<String> optional = list.stream().filter(it->it.contains(name + PayConstants.SYMBOL_EQUAL + PayConstants.SYMBOL_QUOTES)).findFirst();
        if(optional.isPresent()){
            String str = optional.get();
            return str.substring(str.indexOf(PayConstants.SYMBOL_QUOTES) + 1,str.lastIndexOf(PayConstants.SYMBOL_QUOTES));
        }
        return null;
    }

}

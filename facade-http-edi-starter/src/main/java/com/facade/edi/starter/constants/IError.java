package com.facade.edi.starter.constants;
import java.io.Serializable;

/**
 * 响应码定义
 *
 * @author typhoon
 * @since V2.0.0
 */
public interface IError extends Serializable {

    IError SUCCESS = new IError() {
        @Override
        public int getCode() {
            return 0;
        }

        @Override
        public String getMsg() {
            return "success";
        }
    };
   IError SYSTEM_ERROR = new IError() {
        @Override
        public int getCode() {
            return 500;
        }

        @Override
        public String getMsg() {
            return "system error";
        }
    };

    IError UNSUPPORTED_METHOD = new IError() {
        @Override
        public int getCode() {
            return 501;
        }

        @Override
        public String getMsg() {
            return "unSupport method";
        }
    };

    IError PARAM_ILLEGAL = new IError() {
        @Override
        public int getCode() {
            return 400;
        }

        @Override
        public String getMsg() {
            return "param illegal";
        }
    };

    int getCode();
    String getMsg();

    default IError success() {
        return SUCCESS;
    }
    default IError systemError() {
        return SYSTEM_ERROR;
    }
}

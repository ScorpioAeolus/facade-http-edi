package com.facade.edi.samples.demo.proxy;

import lombok.Data;

import java.util.List;

@Data
public class AddressRiskResult {

    /**
     * success : true
     * msg :
     * data : {"score":3,"hacking_event":"","detail_list":["Interact With Malicious Address","Interact With High-risk Tag Address","Interact With Medium-risk Tag Addresses"],"risk_level":"Low","risk_detail":[{"label":"Tornado.Cash: Router","type":"high_risk","volume":1338984,"address":"0xd90e2f925da726b50c4ed8d0fb90ad053324f31b","percent":0.453},{"label":"Tornado.Cash: L1 Helper","type":"high_risk","volume":1859.7,"address":"0xca0840578f57fe71599d29375e16783424023357","percent":0.001},{"label":"Tornado.Cash: Proxy","type":"high_risk","volume":1487760,"address":"0x722122df12d4e14e13ac3b6895a86e84145b6967","percent":0.503},{"label":"Tornado.Cash: 10 ETH","type":"high_risk","volume":167204.511,"address":"0x910cbd523d972eb0a6f4cae4618ad62622b39dbf","percent":0.057},{"label":"HitBTC","type":"medium_risk","volume":76090.41,"address":"0x9c67e141c0472115aa1b98bd0088418be68fd249","percent":0.026}]}
     */

    private Boolean success;
    private String msg;
    private DataBean data;



    @Data
    public static class DataBean {
        /**
         * score : 3
         * hacking_event :
         * detail_list : ["Interact With Malicious Address","Interact With High-risk Tag Address","Interact With Medium-risk Tag Addresses"]
         * risk_level : Low
         * risk_detail : [{"label":"Tornado.Cash: Router","type":"high_risk","volume":1338984,"address":"0xd90e2f925da726b50c4ed8d0fb90ad053324f31b","percent":0.453},{"label":"Tornado.Cash: L1 Helper","type":"high_risk","volume":1859.7,"address":"0xca0840578f57fe71599d29375e16783424023357","percent":0.001},{"label":"Tornado.Cash: Proxy","type":"high_risk","volume":1487760,"address":"0x722122df12d4e14e13ac3b6895a86e84145b6967","percent":0.503},{"label":"Tornado.Cash: 10 ETH","type":"high_risk","volume":167204.511,"address":"0x910cbd523d972eb0a6f4cae4618ad62622b39dbf","percent":0.057},{"label":"HitBTC","type":"medium_risk","volume":76090.41,"address":"0x9c67e141c0472115aa1b98bd0088418be68fd249","percent":0.026}]
         */

        private Integer score;
        private String hacking_event;
        private String risk_level;
        private List<String> detail_list;
        private List<RiskDetailBean> risk_detail;

        /**
         * 低风险: 0 <= score <= 30
         *
         * @return
         */
        public boolean isLowRisk() {
            return null != score
                    && score.compareTo(0) >=0
                    && score.compareTo(30) <= 0;
        }



    }
    @Data
    public static class RiskDetailBean {
        /**
         * label : Tornado.Cash: Router
         * type : high_risk
         * volume : 1338984
         * address : 0xd90e2f925da726b50c4ed8d0fb90ad053324f31b
         * percent : 0.453
         */

        private String label;
        private String type;
        private int volume;
        private String address;
        private double percent;
    }
}

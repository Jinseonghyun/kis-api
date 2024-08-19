package jin.kisapi.model;

public class IndexData {
    private String rt_cd;      // 성공 실패 여부
    private String msg_cd;     // 응답 코드
    private String msg1;       // 응답 메세지
    private Object output1;    // 응답 상세
    private Object[] output2;  // 조회 기간별 시세

    public Object getOutput1() {
        return output1;
    }
    public void setOutput1(Object output1) {
        this.output1 = output1;
    }
    public Object[] getOutput2() {
        return output2;
    }
    public void setOutput2(Object[] output2) {
        this.output2 = output2;
    }
    public String getRt_cd() {
        return rt_cd;
    }
    public void setRt_cd(String rt_cd) {
        this.rt_cd = rt_cd;
    }
    public String getMsg_cd() {
        return msg_cd;
    }
    public void setMsg_cd(String msg_cd) {
        this.msg_cd = msg_cd;
    }
    public String getMsg1() {
        return msg1;
    }
    public void setMsg1(String msg1) {
        this.msg1 = msg1;
    }
}

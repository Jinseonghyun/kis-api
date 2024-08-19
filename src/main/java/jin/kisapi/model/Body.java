package jin.kisapi.model;

public class Body {
    private String rt_cd;    // 성공 실패 여부
    private String msg_cd;   // 응답 코드
    private String msg1;     // 응답 메시지
    private Object output;   // 응답 상세

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
    public Object getOutput() {
        return output;
    }
    public void setOutput(Object output) {
        this.output = output;
    }

}
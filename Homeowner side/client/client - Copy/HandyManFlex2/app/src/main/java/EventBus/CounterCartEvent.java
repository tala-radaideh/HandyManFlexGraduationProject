package EventBus;

public class CounterCartEvent {
    private boolean succuss;

    public CounterCartEvent(boolean succuss) {
        this.succuss = succuss;
    }

    public boolean isSuccuss() {
        return succuss;
    }

    public void setSuccuss(boolean succuss) {
        this.succuss = succuss;
    }
}

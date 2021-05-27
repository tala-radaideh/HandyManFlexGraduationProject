package EventBus;

import com.example.handymanflex.Model.ServiceModel;

public class ServiceItemClick {

private boolean success;
private ServiceModel serviceModel;


    public ServiceItemClick(boolean success, ServiceModel serviceModel) {
        this.success = success;
        this.serviceModel = serviceModel;


    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ServiceModel getServiceModel() {
        return serviceModel;
    }

    public void setServiceModel(ServiceModel serviceModel) {
        this.serviceModel = serviceModel;
    }
}

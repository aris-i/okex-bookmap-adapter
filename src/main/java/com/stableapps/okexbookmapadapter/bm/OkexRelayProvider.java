package com.stableapps.okexbookmapadapter.bm;

import velox.api.layer0.annotations.Layer0LiveModule;
import velox.api.layer1.Layer1ApiProvider;
import velox.api.layer1.annotations.Layer1ApiVersion;
import velox.api.layer1.annotations.Layer1ApiVersionValue;
import velox.api.layer1.common.ListenableHelper;
import velox.api.layer1.common.Log;
import velox.api.layer1.data.Layer1ApiProviderSupportedFeatures;
import velox.api.layer1.data.LoginData;
import velox.api.layer1.data.SubscribeInfo;
import velox.api.layer1.data.UserPasswordDemoLoginData;
import velox.api.layer1.layers.Layer1ApiRelay;


@Layer0LiveModule(fullName = "OKEx", shortName = "Ox")
@Layer1ApiVersion(Layer1ApiVersionValue.VERSION2)
public class OkexRelayProvider extends Layer1ApiRelay {

    public OkexRelayProvider() {
        super(new OkexRealTimeProvider(), false);
        Log.info("Relay " + this.hashCode());
    }

    @Override
    public void login(LoginData loginData) {
        UserPasswordDemoLoginData userPasswordDemoLoginData = (UserPasswordDemoLoginData) loginData;
        
        if (userPasswordDemoLoginData.isDemo) {
            Layer1ApiProvider provider = new OkexRealTimeProvider();
            setProvider(provider);
            Log.info("OkexRealtimeProvider " + provider.hashCode());
        } else {
            Layer1ApiProvider provider = new OkexRealTimeTradingProvider();
            setProvider(provider);
            Log.info("OkexRealtimeTradingProvider " + provider.hashCode());
        }
        ListenableHelper.addListeners(provider, this);
        super.login(loginData);
    }

    @Override
    public Layer1ApiProviderSupportedFeatures getSupportedFeatures() {
        return provider.getSupportedFeatures();
    }
    
    @Override
    public void close() {
        super.close();
        ListenableHelper.removeListeners(provider, this);
        provider.close();
    }
    
    @Override
    public void subscribe(SubscribeInfo subscribeInfo) {
        Log.info("Relay " + this.hashCode());
        Log.info("provider " + provider.hashCode());
        provider.subscribe(subscribeInfo);
    }
}

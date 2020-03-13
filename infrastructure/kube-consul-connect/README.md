# Consul connect for spring boot

Installing consul-connect with helm.

- git clone https://github.com/hashicorp/consul-helm.git
- cd consul-helm
- git checkout tags/v0.17.0 -b v0.17.0
- touch desktop-values.yml
    ```
    # helm install 
    global:
        datacenter: dc1
    ui:
        service:
            type: "NodePort"
    connectInject:
        enabled: true

    client:
        enabled: true
        grpc: true

    server:
        replicas: 1
        bootstrapExpect: 1
        disruptionBudget:
            enabled: true
            maxUnavailable: 1
    ```

- helm install  hashicorp -f desktop-values.yaml .

- helm uninstall hashicorp

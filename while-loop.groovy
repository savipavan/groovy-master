void executeRestart(String action, String cluster, String appName, String namespace) {
    steps.stage("Executing Action - ${action} Cluster - ${cluster} Namespace - ${namespace} Appname -${appName}") {
        String controllerKind = getControllerKind(namespace, appName)
        Integer availableReplicas = getReplicas(appName as String, namespace, controllerKind)
        steps.echo("Available Replicas: ${availableReplicas}")
        Integer readyReplicas = 0

        if (controllerKind.equalsIgnoreCase('replicaset')) {
            // Setting replicas to zero for restart and scaling back to available replicas
            steps.sh "kubectl scale --replicas=${readyReplicas} ${controllerKind}/${appName} --namespace=${namespace}"
            validateReadyReplicas(readyReplicas, availableReplicas, appName, namespace, controllerKind)
            steps.sh "kubectl scale --replicas=${availableReplicas} ${controllerKind}/${appName} --namespace=${namespace}"
            validateReadyReplicas(availableReplicas, readyReplicas, appName, namespace, controllerKind)
        } else {
            // Restart the rollout
            steps.sh "kubectl rollout restart ${controllerKind}/${appName} --namespace=${namespace}"

            // Initial rollout status check with timeout
            boolean isRolloutComplete = checkRolloutStatus(controllerKind, appName, namespace)

            // If rollout is not complete, ask the user to extend the timeout
            while (!isRolloutComplete) {
                Boolean userInput = steps.input(
                    message: "Rollout status verification is still in progress. Do you want to extend the timeout for another 10 minutes?",
                    parameters: [steps.booleanParam(name: 'extendTimeout', defaultValue: true, description: 'Extend timeout?')]
                )

                if (userInput) {
                    steps.echo("Extending the timeout for another 10 minutes...")
                    isRolloutComplete = checkRolloutStatus(controllerKind, appName, namespace)
                } else {
                    steps.error("Rollout status verification aborted by user.")
                }
            }
        }
    }
}

boolean checkRolloutStatus(String controllerKind, String appName, String namespace) {
    boolean isRolloutComplete = false
    steps.timeout(time: Constants.ROLLOUT_STATUS_TIMEOUT, unit: 'SECONDS') {
        steps.sh "kubectl rollout status ${controllerKind}/${appName} --namespace=${namespace}"
        isRolloutComplete = true  // Mark as complete if this point is reached
    }
    return isRolloutComplete
}

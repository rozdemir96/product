# Product Management Application - Kubernetes Deployment Guide

Bu rehber, Product Management uygulamasını Kubernetes ortamında çalıştırmak için adım adım talimatları içerir.

## Gereksinimler

- Docker
- Minikube
- kubectl
- Java 21
- Gradle

### 1. Minikube'u Başlatma

```bash
# Minikube'u başlat
minikube start --memory=4096 --cpus=2 --driver=docker

# Minikube'un durumunu kontrol et
minikube status
```

Minikube başarıyla başlatıldığında, `minikube status` komutu aşağıdakine benzer bir çıktı gösterecektir:
```
minikube
type: Control Plane
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured
```

### 2. Minikube Docker Daemon'ını Kullanma

```bash
# Minikube'un Docker daemon'ını kullanmak için ortam değişkenlerini ayarla
# Windows PowerShell için:
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Linux/Mac için:
eval $(minikube docker-env)
```

### 3. PostgreSQL Image'ini Çekme

```bash
# PostgreSQL image'ini çek
docker pull postgres:16
```

### 4. Backend Uygulamasını Hazırlama ve Docker İmajını Oluşturma

```bash
# Backend klasörüne git
cd backend

# Uygulamayı derle ve Docker imajını oluştur (Minikube Docker daemon'ını kullanarak)
./gradlew clean build bootBuildImage

# Ana klasöre geri dön
cd ..
```

### 5. Frontend Uygulamasını Hazırlama ve Docker İmajını Oluşturma

```bash
# Frontend klasörüne git
cd frontend

# Docker imajını oluştur (Minikube Docker daemon'ını kullanarak)
docker build -t frontend:latest .

# Ana klasöre geri dön
cd ..
```

### 6. Kubernetes Kaynaklarını Oluşturma

```bash
# PostgreSQL için kalıcı depolama alanını oluştur
kubectl apply -f k8s/postgres/postgres-pv-pvc.yaml

# PostgreSQL deployment ve service'i oluştur
kubectl apply -f k8s/postgres/postgres-deployment.yaml

# Backend ConfigMap'i oluştur (test verilerini ve Liquibase'i etkinleştirmek için)
kubectl apply -f k8s/backend/backend-config.yaml

# Backend deployment ve service'i oluştur
kubectl apply -f k8s/backend/backend-deployment.yaml

# Frontend ConfigMap'i oluştur
kubectl apply -f k8s/frontend/frontend-configmap.yaml

# Frontend deployment ve service'i oluştur
kubectl apply -f k8s/frontend/frontend-deployment.yaml
```

### 7. Pod'ların Durumunu Kontrol Etme

```bash
# Tüm pod'ları listele
kubectl get pods

# Pod'ların hazır olmasını bekle
# Tüm pod'lar "Running" durumunda olmalı
```

### 8. Uygulamalara Erişim

```bash
# Backend servisine port-forward yap
kubectl port-forward service/backend 8080:8080

# Yeni bir terminal aç ve frontend servisine port-forward yap
kubectl port-forward service/frontend 8082:80
```

### 9. Tarayıcıda Uygulamaya Erişim

- Frontend: http://localhost:8082
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### 10. Test Verilerini Devre Dışı Bırakma (İlk Çalıştırmadan Sonra)

```bash
# ConfigMap'i güncelle (PowerShell için)
kubectl edit configmap backend-config
# Açılan editörde APP_LOAD_TEST_DATA ve SPRING_LIQUIBASE_ENABLED değerlerini "false" olarak değiştirin

# Backend deployment'ını yeniden başlat
kubectl rollout restart deployment backend
```

## Kullanıcı Bilgileri

- Kullanıcı adı: admin
- Şifre: 123456

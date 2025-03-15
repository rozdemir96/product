# Product UI - Docker Kurulumu

Bu belge, Product UI (Angular) uygulamasını Docker kullanarak nasıl çalıştıracağınızı açıklar.

## Gereksinimler

- Docker
- Docker Compose

## Kurulum Adımları

### Frontend'i Çalıştırma

Frontend'i çalıştırmak için:

```bash
# Angular projenizin kök dizininde
docker-compose up -d
```

Bu komut:
- Angular uygulamanızı build edip bir Docker image oluşturacak
- Uygulamayı 4200 portunda servis edecek
- Backend'e bağlanmak için gerekli proxy ayarlarını yapacak

## Erişim Bilgileri

- **Frontend (Angular)**: http://localhost:4200
- **API Endpoint**: http://localhost:4200/api/... (Frontend üzerinden proxy ile)

## Uygulamayı Durdurma

```bash
docker-compose down
```

## Yeniden Build Etme

Frontend'de değişiklik yaptıysanız:

```bash
docker-compose build frontend
docker-compose up -d
```

## Logları Görüntüleme

```bash
# Frontend logları
docker-compose logs -f frontend
```

## Sorun Giderme

1. **API bağlantı hatası**: nginx.conf dosyasındaki proxy_pass ayarının doğru olduğundan emin olun.

2. **Container başlatılamıyor**: Docker Compose dosyasındaki servis isimlerinin ve port eşlemelerinin doğru olduğundan emin olun.

3. **Network hatası**: `product-net` ağının backend compose dosyasında oluşturulduğundan emin olun.

4. **IP adresi çakışması**: Eğer 162.25.0.0/24 subnet'i başka bir ağ ile çakışıyorsa, backend compose dosyasında subnet değerini değiştirin. 
![헤더](https://capsule-render.vercel.app/api?type=speech&height=200&color=000000&text=📍어디야?&animation=twinkling&fontColor=ffffff&fontAlignY=35&desc=ver.1.0.0&descSize=25&descAlignY=63&section=header)
# 기술 스택
- **구조**: MVVM, Clean Architecture지향 / [🔗구조도](docs/whereRU_architecture.png)
- **기본**: Kotlin, Coroutine, Hilt, Room, Retrofit
- **빌드**: Multi Module, Precompiled Scripts
- **CI/CD**: GitHub Actions, Firebase App Distribution / [👥테스터 초대 링크](https://appdistribution.firebase.dev/i/fda00582e7173283)
- **테스트**: junit, mockk
- **SDK**:
  - **Google**: maps, location, oauth2
  - **Firebase**: FCM, Realtime Database, auth, firestore
  - **ETC**: shimmer, colorpickerpreference
# 특징
### Flow + DataResource 기반 API 상태 관리 및 에러 핸들링
- **선언** [🔗DataResource.kt](https://github.com/miiiin15/whereRU/blob/release/mvp/data-resource/src/main/java/com/miiiin15/whereru/data_resource/DataResource.kt#L3)
```kotlin
sealed class DataResource<out T> {
    class Success<T>(val data: T) : DataResource<T>()
    class Error(val throwable: Throwable) : DataResource<Nothing>()
    class Loading<T>(val data: T? = null) : DataResource<T>() // 로딩중 보일 데이터(캐싱과 연계)
    ...생략
```
- **확장** [🔗FlowDataResourceExtension.kt](https://github.com/miiiin15/whereRU/blob/1f45c0b3fd715a5f19d0ae4abd07a4fb3c81145b/data-resource/src/main/java/com/miiiin15/whereru/data_resource/FlowDataResourceExtension.kt#L11-L25)
```kotlin
suspend fun <T> Flow<DataResource<T>>.collectDataResource(
  onSuccess: suspend (T) -> Unit,
  onError: (Throwable) -> Unit,
  onLoading: (T?) -> Unit = {},
) {
  this.catch { onError(it) }
    .collect {
      when (it) {
        is DataResource.Success -> onSuccess(it.data)
        is DataResource.Error -> onError(it.throwable)
        is DataResource.Loading -> onLoading.invoke(it.data)
      }
    }
}
```
- **사용** [🔗LiveLocationViewModel.kt](https://github.com/miiiin15/whereRU/blob/1f45c0b3fd715a5f19d0ae4abd07a4fb3c81145b/presentation/src/main/kotlin/com/miiiin15/whereru/presentation/viewmodel/LiveLocationViewModel.kt#L50-L58)
```kotlin
    fun fetchProfile(uid: String) = launch {
      getProfileUseCase(uid)
      .mapDataResource { it.toPresentation() } // UI에 호환 되는 모델로 매핑하는 작업
      .collectDataResource(
        onSuccess = { profile ->
          _myProfile.value = profile
        },
        loadingEnable = false
      )
    }
```

### Room과 연계 오프라인 캐싱을 통한 UX 개선
- **확장** [🔗FlowPersistableRemoteBoundResource.kt](data/src/main/kotlin/com/miiiin15/whereru/data/bound/FlowPersistableRemoteBoundResource.kt)
```kotlin
class FlowPersistableRemoteBoundResource<DomainType, DataType>(
    dataAction: suspend () -> DataType,
    private val localDataAction: suspend () -> DataType?,
    private val saveCacheAction: suspend (DataType) -> Unit,
) : FlowBaseBoundResource<DomainType, DataType>(dataAction) {

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<DataResource<DomainType>>) {
        try {
            val localData: DomainType? =
                try {
                    localDataAction()?.toDomainModel() // local값 있으면
                } catch (exception: Exception) {
                    null
                }
            collector.emit(DataResource.loading(localData)) // 전달
            fetchFromSource(collector, saveCacheAction)
        } catch (exception: Exception) {
            collector.emit(DataResource.error(exception))
        }
    }

}
```
- **사용** [🔗ProfileRepositoryImpl.kt](https://github.com/miiiin15/whereRU/blob/1f45c0b3fd715a5f19d0ae4abd07a4fb3c81145b/data/src/main/kotlin/com/miiiin15/whereru/data/impl/ProfileRepositoryImpl.kt#L23-L31)
```kotlin
override fun getPaginatedProfiles(
        ...
    ): Flow<DataResource<List<User>>> =
        flowDataResource(
            { /** remote api 요청 **/ },
            { /** 응답 대기중 local(Room) 미리보기 데이터 꺼내오기 **/ },
            { /** api 응답 값 캐싱 **/ },
        )
```
- **응용** [🔗HomeFragment.kt](https://github.com/miiiin15/whereRU/blob/1f45c0b3fd715a5f19d0ae4abd07a4fb3c81145b/ui/src/main/kotlin/com/miiiin15/whereru/ui/home/HomeFragment.kt#L171-L174)
```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition() // 화면 전환 지연
	}
	
	sessionList observe { data -> // local값 수신 받아서 트리거
		...
		sessionListAdapter.recyclerView.doOnPreDraw {
			startPostponedEnterTransition() // 재개
		}
		...
	}
```
<table>
  <thead>
    <tr>
      <td colspan="2" align="center">비교화면 0.5배속</td>
    </tr>
    <tr>
      <th>개선 전<br>(로그인 → 화면 전환 → 공백 → 요청 → 로딩 → 제공)</th>
      <th>개선 후<br>(로그인 → 로딩 → 요청 → 화면 전환&제공)</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><img src="https://github.com/user-attachments/assets/a6df4213-ee87-4608-a4a3-8e07f5b8c83f" alt="before" height="700"/></td>
      <td><img src="https://github.com/user-attachments/assets/07dc4103-043f-4659-ba05-7eac19e3166e" alt="after" height="700"/></td>
    </tr>
  </tbody>
</table>

### 주요 RecyclerView에 DiffUtil.Callback 적용을 통한 최적화
- **선언** [🔗UserDiffCallback.kt](ui/src/main/kotlin/com/miiiin15/whereru/ui/home/UserDiffCallback.kt)
- **사용** [🔗UserListAdapter.kt](https://github.com/miiiin15/whereRU/blob/1f45c0b3fd715a5f19d0ae4abd07a4fb3c81145b/ui/src/main/kotlin/com/miiiin15/whereru/ui/home/UserListAdapter.kt#L28)
# 기능
- 세션을 개설 또는 참여하여 본인과 참가자들의 위치 변화를 실시간으로 확인
- 특정 사용자에게 푸시 메시지(FCM)를 통한 공유 요청 및 답장
- [🔗in-app 화면](docs/whereRU_in_app.md)
# 환경
- **Kotlin**: 1.9.25
- **AGP**: 8.3.2
- **Coroutines**: 1.9.0
- **Dagger:Hilt (DI)**: 2.51.1
- **Google Maps Compose**: 5.0.0

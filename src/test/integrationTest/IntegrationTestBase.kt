import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader
import jp.mangaka.ssp.CompassManagerApplication
import jp.mangaka.ssp.infrastructure.datasource.config.CompassLogDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CompassMasterReplicaDbConfig
import jp.mangaka.ssp.infrastructure.datasource.config.CoreMasterDbConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(classes = [CompassManagerApplication::class])
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        CoreMasterDbConfig::class,
        CompassMasterDbConfig::class,
        CompassMasterReplicaDbConfig::class,
        CompassLogDbConfig::class
    ]
)
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    DirtiesContextTestExecutionListener::class,
    TransactionDbUnitTestExecutionListener::class
)
@DbUnitConfiguration(
    databaseConnection = [
        "CoreMasterDS",
        "CompassMasterDS",
        "CompassMasterReplicaDS",
        "CompassLogDS"
    ],
    dataSetLoader = ReplacementDataSetLoader::class
)
abstract class IntegrationTestBase {
    @Autowired
    lateinit var mockMvc: MockMvc
}

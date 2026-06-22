package com.travel.config;

import com.travel.entity.*;
import com.travel.repository.*;
import com.travel.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GuideRepository guideRepository;
    private final TourRouteRepository tourRouteRepository;
    private final TouristRepository touristRepository;
    private final RegistrationRepository registrationRepository;
    private final RegistrationService registrationService;

    @Override
    @Transactional
    public void run(String... args) {
        if (guideRepository.count() > 0) {
            log.info("数据已存在，跳过初始化");
            return;
        }

        log.info("开始初始化示例数据...");

        initGuides();
        List<TourRoute> tours = initTourRoutes();
        List<Tourist> tourists = initTourists();
        initRegistrations(tours, tourists);

        log.info("示例数据初始化完成");
    }

    private void initGuides() {
        Guide g1 = new Guide();
        g1.setName("张伟");
        g1.setPhone("13800138001");
        g1.setEmail("zhangwei@travel.com");
        g1.setLanguages(Set.of("中文", "英语"));
        g1.setLicenseNumber("D-G-2024-001");
        g1.setNotes("资深英语导游，擅长欧美线路");
        g1.setActive(true);
        guideRepository.save(g1);

        Guide g2 = new Guide();
        g2.setName("李娜");
        g2.setPhone("13800138002");
        g2.setEmail("lina@travel.com");
        g2.setLanguages(Set.of("中文", "日语"));
        g2.setLicenseNumber("D-G-2024-002");
        g2.setNotes("日语专业，日本通");
        g2.setActive(true);
        guideRepository.save(g2);

        Guide g3 = new Guide();
        g3.setName("王芳");
        g3.setPhone("13800138003");
        g3.setEmail("wangfang@travel.com");
        g3.setLanguages(Set.of("中文", "韩语"));
        g3.setLicenseNumber("D-G-2024-003");
        g3.setNotes("韩国留学归来，熟悉韩国文化");
        g3.setActive(true);
        guideRepository.save(g3);

        Guide g4 = new Guide();
        g4.setName("陈强");
        g4.setPhone("13800138004");
        g4.setEmail("chenqiang@travel.com");
        g4.setLanguages(Set.of("中文", "法语", "英语"));
        g4.setLicenseNumber("D-G-2024-004");
        g4.setNotes("欧洲游专家，会法语和英语");
        g4.setActive(true);
        guideRepository.save(g4);

        Guide g5 = new Guide();
        g5.setName("赵敏");
        g5.setPhone("13800138005");
        g5.setEmail("zhaomin@travel.com");
        g5.setLanguages(Set.of("中文", "德语"));
        g5.setLicenseNumber("D-G-2024-005");
        g5.setNotes("德语导游，擅长中欧线路");
        g5.setActive(true);
        guideRepository.save(g5);

        log.info("已创建 5 名示例导游");
    }

    private List<TourRoute> initTourRoutes() {
        LocalDate today = LocalDate.now();

        TourRoute t1 = new TourRoute();
        t1.setName("【日本】东京-大阪-京都 6日经典游");
        t1.setDescription("畅游日本三大都市，体验传统与现代的完美融合");
        t1.setItinerary("""
            第1天：上海→东京，入住酒店，浅草寺观光
            第2天：东京迪士尼乐园一日游
            第3天：新宿、涩谷、秋叶原自由活动
            第4天：东京→京都，金阁寺、岚山竹林
            第5天：京都→大阪，大阪城、道顿堀
            第6天：大阪→上海，结束愉快旅程
            """);
        t1.setDepartureDate(today.plusDays(15));
        t1.setReturnDate(today.plusDays(20));
        t1.setPrice(new BigDecimal("6999.00"));
        t1.setMinPeople(10);
        t1.setMaxPeople(30);
        t1.setDepartureCity("上海");
        t1.setDestination("日本");
        t1.setStatus(TourRoute.TourStatus.PENDING);
        tourRouteRepository.save(t1);

        TourRoute t2 = new TourRoute();
        t2.setName("【泰国】曼谷-芭提雅 5日休闲游");
        t2.setDescription("体验泰国热带风情，享受阳光沙滩");
        t2.setItinerary("""
            第1天：上海→曼谷，大皇宫、玉佛寺
            第2天：曼谷→芭提雅，东芭乐园
            第3天：格兰岛一日游
            第4天：芭提雅自由活动，人妖秀
            第5天：芭提雅→曼谷→上海
            """);
        t2.setDepartureDate(today.plusDays(7));
        t2.setReturnDate(today.plusDays(11));
        t2.setPrice(new BigDecimal("3999.00"));
        t2.setMinPeople(8);
        t2.setMaxPeople(25);
        t2.setDepartureCity("上海");
        t2.setDestination("泰国");
        t2.setStatus(TourRoute.TourStatus.PENDING);
        tourRouteRepository.save(t2);

        TourRoute t3 = new TourRoute();
        t3.setName("【韩国】首尔-济州岛 5日游");
        t3.setDescription("韩流文化之旅，购物美食两不误");
        t3.setItinerary("""
            第1天：上海→首尔，明洞购物
            第2天：景福宫、南山塔、北村韩屋村
            第3天：首尔→济州岛，城山日出峰
            第4天：济州岛自由活动
            第5天：济州岛→上海
            """);
        t3.setDepartureDate(today.plusDays(20));
        t3.setReturnDate(today.plusDays(24));
        t3.setPrice(new BigDecimal("4599.00"));
        t3.setMinPeople(8);
        t3.setMaxPeople(25);
        t3.setDepartureCity("上海");
        t3.setDestination("韩国");
        t3.setStatus(TourRoute.TourStatus.PENDING);
        tourRouteRepository.save(t3);

        TourRoute t4 = new TourRoute();
        t4.setName("【法瑞意】欧洲三国 10日经典游");
        t4.setDescription("浪漫巴黎、山水瑞士、时尚意大利一次游遍");
        t4.setItinerary("""
            第1-2天：巴黎：埃菲尔铁塔、卢浮宫、凡尔赛宫
            第3-4天：瑞士：少女峰、因特拉肯
            第5-7天：意大利：威尼斯、佛罗伦萨
            第8-9天：罗马：斗兽场、梵蒂冈
            第10天：罗马→上海
            """);
        t4.setDepartureDate(today.plusDays(30));
        t4.setReturnDate(today.plusDays(39));
        t4.setPrice(new BigDecimal("19999.00"));
        t4.setMinPeople(12);
        t4.setMaxPeople(25);
        t4.setDepartureCity("上海");
        t4.setDestination("欧洲");
        t4.setStatus(TourRoute.TourStatus.PENDING);
        tourRouteRepository.save(t4);

        TourRoute t5 = new TourRoute();
        t5.setName("【国内】云南大理-丽江-香格里拉 6日游");
        t5.setDescription("七彩云南，探寻少数民族风情");
        t5.setItinerary("""
            第1天：上海→昆明→大理
            第2天：大理古城、洱海
            第3天：大理→丽江，丽江古城
            第4天：玉龙雪山
            第5天：丽江→香格里拉，普达措国家公园
            第6天：香格里拉→上海
            """);
        t5.setDepartureDate(today.plusDays(10));
        t5.setReturnDate(today.plusDays(15));
        t5.setPrice(new BigDecimal("3899.00"));
        t5.setMinPeople(6);
        t5.setMaxPeople(20);
        t5.setDepartureCity("上海");
        t5.setDestination("云南");
        t5.setStatus(TourRoute.TourStatus.PENDING);
        tourRouteRepository.save(t5);

        TourRoute t6 = new TourRoute();
        t6.setName("【海南】三亚 5日海岛度假游");
        t6.setDescription("阳光沙滩，椰林树影，放松身心的海岛之旅");
        t6.setItinerary("""
            第1天：上海→三亚，入住海景酒店
            第2天：蜈支洲岛一日游
            第3天：南山寺、天涯海角
            第4天：亚龙湾自由活动
            第5天：三亚→上海
            """);
        t6.setDepartureDate(today.plusDays(5));
        t6.setReturnDate(today.plusDays(9));
        t6.setPrice(new BigDecimal("2999.00"));
        t6.setMinPeople(5);
        t6.setMaxPeople(20);
        t6.setDepartureCity("上海");
        t6.setDestination("三亚");
        t6.setStatus(TourRoute.TourStatus.PENDING);
        tourRouteRepository.save(t6);

        log.info("已创建 6 条示例线路");
        return tourRouteRepository.findAll();
    }

    private List<Tourist> initTourists() {
        String[] names = {"王小明", "李小红", "张小华", "刘小强", "陈小燕",
            "杨小伟", "赵小丽", "黄小峰", "周小敏", "吴小军",
            "徐小芳", "孙小亮", "马小娟", "朱小波", "胡小琳"};

        for (int i = 0; i < names.length; i++) {
            Tourist t = new Tourist();
            t.setName(names[i]);
            t.setPhone("13900" + String.format("%06d", i + 1));
            t.setEmail("tourist" + (i + 1) + "@example.com");
            t.setIdCardNumber("310101199" + (i % 9) + "0101" + String.format("%04d", i + 1));
            touristRepository.save(t);
        }

        log.info("已创建 15 名示例游客");
        return touristRepository.findAll();
    }

    private void initRegistrations(List<TourRoute> tours, List<Tourist> tourists) {
        int registrationCount = 0;

        TourRoute japanTour = tours.stream().filter(t -> t.getName().contains("日本")).findFirst().orElse(null);
        if (japanTour != null) {
            for (int i = 0; i < 7; i++) {
                Registration reg = createRegistration(japanTour, tourists.get(i), 1 + (i % 2));
                registrationRepository.save(reg);
                registrationCount++;
            }
            registrationService.checkAndFormGroup(japanTour);
        }

        TourRoute thailandTour = tours.stream().filter(t -> t.getName().contains("泰国")).findFirst().orElse(null);
        if (thailandTour != null) {
            for (int i = 7; i < 12; i++) {
                Registration reg = createRegistration(thailandTour, tourists.get(i), 1 + (i % 3));
                registrationRepository.save(reg);
                registrationCount++;
            }
            registrationService.checkAndFormGroup(thailandTour);
        }

        TourRoute koreaTour = tours.stream().filter(t -> t.getName().contains("韩国")).findFirst().orElse(null);
        if (koreaTour != null) {
            for (int i = 3; i < 7; i++) {
                Registration reg = createRegistration(koreaTour, tourists.get(i), 2);
                registrationRepository.save(reg);
                registrationCount++;
            }
            registrationService.checkAndFormGroup(koreaTour);
        }

        TourRoute yunnanTour = tours.stream().filter(t -> t.getName().contains("云南")).findFirst().orElse(null);
        if (yunnanTour != null) {
            for (int i = 10; i < 14; i++) {
                Registration reg = createRegistration(yunnanTour, tourists.get(i), 1);
                registrationRepository.save(reg);
                registrationCount++;
            }
            registrationService.checkAndFormGroup(yunnanTour);
        }

        TourRoute sanyaTour = tours.stream().filter(t -> t.getName().contains("三亚")).findFirst().orElse(null);
        if (sanyaTour != null) {
            for (int i = 0; i < 5; i++) {
                Registration reg = createRegistration(sanyaTour, tourists.get(i + 5), 1);
                registrationRepository.save(reg);
                registrationCount++;
            }
            registrationService.checkAndFormGroup(sanyaTour);
        }

        log.info("已创建 {} 条示例报名记录", registrationCount);
    }

    private Registration createRegistration(TourRoute tour, Tourist tourist, int peopleCount) {
        Registration reg = new Registration();
        reg.setTourRoute(tour);
        reg.setTourist(tourist);
        reg.setPeopleCount(peopleCount);
        reg.setTotalPrice(tour.getPrice().multiply(new BigDecimal(peopleCount)));
        reg.setStatus(Registration.RegistrationStatus.CONFIRMED);
        reg.setRemarks("示例报名数据");
        return reg;
    }
}

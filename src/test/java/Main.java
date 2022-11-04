import net.bytebuddy.asm.Advice;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class Main extends BaseTest{

    @BeforeMethod void bfMethod(){
        driver.navigate().to("http://yaz.tf.firat.edu.tr/tr");
    }

    @Test void headerIcon(){
        driver.findElement(By.xpath("//img[@class='desktop-logo']")).click();
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.firat.edu.tr/tr");
    }

    @Test void search(){
        String value = "Resul Das";
        driver.findElement(By.xpath("(//div[@class='search-inner-button'])[2]")).click();
        driver.findElement(By.xpath("//input[@type='text']")).sendKeys(value, Keys.ENTER);
        boolean result = driver.findElement(By.xpath("(//h1[@class='search-result-title'])[2]")).getText().split(" için ")[0].contains(value);
        Assert.assertTrue(result, "Wrong search value result"); //too many search results
    }

    @Test void searchBoxEmptyChar(){
        driver.findElement(By.xpath("(//div[@class='search-inner-button'])[2]")).click();
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Assert.assertEquals(driver.getCurrentUrl(), "http://yaz.tf.firat.edu.tr/tr", "empty search box");
    }

    @Test void searchBoxCloseButton(){
        driver.findElement(By.xpath("(//div[@class='search-inner-button'])[2]")).click();
        driver.findElement(By.xpath("//i[@class='fas fa-times close-icon']")).click();
        Assert.assertTrue(driver.findElement(By.xpath("//i[@class='fas fa-times close-icon']")).isDisplayed(), "close button not working");
    }

    @Test void checkLanguage(){
        String tr = driver.findElement(By.xpath("((//li[@class='language-button'])[1]//a)[1]")).getAttribute("href");
        String en = driver.findElement(By.xpath("((//li[@class='language-button'])[1]//a)[2]")).getAttribute("href");
        soft.assertEquals(tr,"http://yaz.tf.firat.edu.tr/tr", "tr has error");
        soft.assertEquals(en,"http://yaz.tf.firat.edu.tr/en", "en has error");

        soft.assertAll();
    }

    @Test void AcademicAndAdminPersonel(){
        act.moveToElement(driver.findElement(By.xpath("(//ul[@id='nav']//li)[9]"))).build().perform();
        act.moveToElement(driver.findElement(By.xpath("(//ul[@id='nav']//li)[10]"))).build().perform();
        soft.assertEquals(driver.getCurrentUrl(), "http://yaz.tf.firat.edu.tr/tr/academic-staffs");

        driver.findElement(By.xpath("//span[@class='slider round']")).click();
        soft.assertEquals(driver.getCurrentUrl(), "http://yaz.tf.firat.edu.tr/tr/admin-staffs");
    }

    @Test(dataProvider = "NavigatorButtons") void topNavigationBar(String xpath, String expected){
        try {
            for (int i = 1; i <= 10; i++) {
                act.moveToElement(driver.findElement(By.xpath("(//ul[@id='nav']/li)[" + i + "]"))).build().perform();

                if (driver.findElement(By.xpath(xpath)).isDisplayed()) break;
            }
            driver.findElement(By.xpath(xpath)).click();
            Assert.assertEquals(driver.getCurrentUrl(), expected);

            List<WebElement> links = driver.findElements(By.xpath("//body//div[@class='detail']//a"));
            for (WebElement a: links) {
                String url = a.getAttribute("href");
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("HEAD");
                conn.connect();
                soft.assertTrue(conn.getResponseCode() < 400,a.getText() + "          BROKEN LINK");
            }

            soft.assertAll();

        } catch (ClassCastException | ElementNotInteractableException | IOException exception){}
    }

    @Test(dataProvider = "fastAccessButtons") void fastAccess(String xpath, String expected){
        try {
            for (int i = 1; i <= 5; i++) {
                act.click(driver.findElement(By.xpath("(//ul[@class='fast-access-menu']//i)[" + i + "]"))).build().perform();

                if (driver.findElement(By.xpath(xpath)).isDisplayed()) break;
            }
            driver.findElement(By.xpath(xpath)).click();
            Assert.assertEquals(driver.getCurrentUrl(), expected);

            List<WebElement> links = driver.findElements(By.xpath("//body//div[@class='detail']//a"));
            for (WebElement a: links) {
                String url = a.getAttribute("href");
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("HEAD");
                conn.connect();
                soft.assertTrue(conn.getResponseCode() < 400,a.getText() + "          BROKEN LINK");
            }

            soft.assertAll();

        } catch (ClassCastException | ElementNotInteractableException | IOException exception){}
    }

    @DataProvider Object[][] NavigatorButtons(){
        Object[][] nav = new Object[31][2];

        for (int i = 1; i < 31; i++) nav[i][0] = "(//ul[@id='nav']//li)[" + i +  "]";
        
        nav[1][1]  = "http://yaz.tf.firat.edu.tr/tr"; // Ana sayfa
        nav[2][1]  = "http://yaz.tf.firat.edu.tr/tr/page/545"; // Hakkimizda
        nav[3][1]  = "http://yaz.tf.firat.edu.tr/tr/page/545"; // Genel Bilgi
        nav[4][1]  = "http://yaz.tf.firat.edu.tr/tr/page/546"; // Bolum Baskanimiz
        nav[5][1]  = "http://yaz.tf.firat.edu.tr/tr/page/547"; // Bolum Baskan Yardimcilari
        nav[6][1]  = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/kvk/Yazilim_Muh_Bolum-Gorevleri.pdf"; // Koordinattorler
        nav[7][1]  = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/kvk/Yazilim_Muh_Bolum-Gorevleri.pdf"; // Komisyonlar
        nav[8][1]  = ""; // Personel
        nav[9][1]  = ""; // empty
        nav[10][1] = "http://yaz.tf.firat.edu.tr/tr/academic-staffs"; // Akademik
        nav[11][1] = "http://yaz.tf.firat.edu.tr/tr/admin-staffs"; // Idari
        nav[12][1] = "http://yaz.tf.firat.edu.tr/tr/page/550"; // Lisans
        nav[13][1] = "http://yaz.tf.firat.edu.tr/tr/page/550"; // Genel Bilgi
        nav[14][1] = "http://yaz.tf.firat.edu.tr/tr/page/551"; // Ders Listesi
        nav[15][1] = "http://yaz.tf.firat.edu.tr/tr/page/552"; // Ders Icerikleri
        nav[16][1] = "http://yaz.tf.firat.edu.tr/tr/page/553"; // Ders Programi (hala eski program)
        nav[17][1] = "http://yaz.tf.firat.edu.tr/tr/page/554"; // Lisans ustu
        nav[18][1] = "http://yaz.tf.firat.edu.tr/tr/page/554"; // Genel Bilgi
        nav[19][1] = "http://yaz.tf.firat.edu.tr/tr/page/555"; // Ders Listesi
        nav[20][1] = "http://yaz.tf.firat.edu.tr/tr/page/556"; // Ders Icerikleri
        nav[21][1] = "http://yaz.tf.firat.edu.tr/tr/page/557"; // Arastirma
        nav[22][1] = "http://yaz.tf.firat.edu.tr/tr/page/557"; // Proje
        nav[23][1] = "http://yaz.tf.firat.edu.tr/tr/page/558"; // Laboratuvarlar
        nav[24][1] = "http://yaz.tf.firat.edu.tr/tr/page/559"; // Yayinlar (bos)
        nav[25][1] = "http://yaz.tf.firat.edu.tr/tr/page/560"; // Kaynaklar
        nav[26][1] = "http://yaz.tf.firat.edu.tr/tr/page/560"; // Dokumanlar
        nav[27][1] = "http://yaz.tf.firat.edu.tr/tr/page/561"; // Yonetmelikler/Yonergeler
        nav[28][1] = "http://yaz.tf.firat.edu.tr/tr/page/562"; // Ders Dokumanlari (bos)
        nav[29][1] = "http://yaz.tf.firat.edu.tr/tr/page/563"; // Iletisim
        nav[30][1] = "https://obs.firat.edu.tr/oibs/bologna/index.aspx?lang=tr&curOp=showPac&curUnit=1&curSunit=1541"; // Bologna

        return nav;
    }

    @DataProvider Object[][] fastAccessButtons(){
        Object[][] fastAccess = new Object[34][2];

        for (int i = 1; i < 34; i++) fastAccess[i][0] = "(//ul[@class='fast-access-menu']//li)["+i+"]";

        fastAccess[1][1]  = ""; // Yazilim Muhendisligi UOLP +
        fastAccess[2][1]  = "http://yazilimuolp.tf.firat.edu.tr/"; // UOLP Yazilim Muhendisligi
        fastAccess[3][1]  = "https://www.shsu.edu/"; // Sam Houston State University
        fastAccess[4][1]  = "http://yaz.tf.firat.edu.tr/tr/page/4309"; // UOLP Programi Bolum Tanitimi
        fastAccess[5][1]  = "http://yaz.tf.firat.edu.tr/tr/page/4310"; // Bitirme Projesi
        fastAccess[6][1]  = "http://yaz.tf.firat.edu.tr/tr/page/9071"; // Mesleki Uygulama (Staj)
        fastAccess[7][1]  = ""; // Is Yeri Egitimi +
        fastAccess[8][1]  = "http://yaz.tf.firat.edu.tr/tr/page/4320"; // Is yeri Egitimi
        fastAccess[9][1]  = "http://yaz.tf.firat.edu.tr/tr/page/4321"; // Is Yeri Egitimi Defter Hazirlama Klavuzu
        fastAccess[10][1] = "http://tf.firat.edu.tr/tr/page/2182"; // Is Yeri Egitimi Evraklari
        fastAccess[11][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/27/Isyeri%20Egitimi%20Yonergesi.pdf"; // Is Yeri Egitimi Yonergesi
        fastAccess[12][1] = "http://yaz.tf.firat.edu.tr/tr/page/4322"; // Paydas Firmalar
        fastAccess[13][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/27/tez_seminer_bilgileri_8.pdf"; // Lisansustu Tezler
        fastAccess[14][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/27/DoktoraYeterlik-ilkeler.pdf"; // Doktora Yeterlik
        fastAccess[15][1] = ""; // Mezunlar +
        fastAccess[16][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/mezun/lisans.pdf"; // Lisans Mezunlar
        fastAccess[17][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/mezun/yuksek-lisans.pdf"; // Yuksek Lisans Mezunlar
        fastAccess[18][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/mezun/doktora.pdf"; // Doktara Mezunlari
        fastAccess[19][1] = "http://yaz.tf.firat.edu.tr/tr/page/4325"; // Mezun Bilgi Formu
        fastAccess[20][1] = ""; // Sikca Sorulan Sorular (SSS) +
        fastAccess[21][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/sss/hazirlik-ve-1-sinif-sss.pdf"; // Hazirlik ve 1.Sinif SSS
        fastAccess[22][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/sss/isyeri-egitimi-ve-staj-sss.pdf"; // Is yeri Egitimi ve Staj SSS
        fastAccess[23][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/sss/ders-kayit-sss.pdf"; // Ders Kayit SSS
        fastAccess[24][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/sss/uzaktan-egitim-sss.pdf"; // Uzaktan Egitim SSS
        fastAccess[25][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/sss/tekcift-ders-sinavi-sss.pdf"; // Tek/Cift Ders Sinavi SSS
        fastAccess[26][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/sss/yaz-okulu-sss.pdf"; // Yaz Okulu SSS
        fastAccess[27][1] = "http://yaz.tf.firat.edu.tr/subdomain_files/yaz.tf.firat.edu.tr/files/1968/sss/yatay-gecis-sss.pdf"; // Yatay Gecis SSS
        fastAccess[28][1] = "https://www.firat.edu.tr/documents/1607005914.pdf"; // Akademik Takvim (eski)
        fastAccess[29][1] = "http://yaz.tf.firat.edu.tr/tr/page/8995"; // Hazir Formlar ve Dilekce Ornekleri
        fastAccess[30][1] = ""; // e-Hizmetler +
        fastAccess[31][1] = "https://jasig.firat.edu.tr/cas/login?target=googleAppsStaff"; // Firat e-Posta
        fastAccess[32][1] = "https://obs.firat.edu.tr/"; // Ogrenci Isleri Otomasyonu
        fastAccess[33][1] = "https://obs.firat.edu.tr/"; // Transkript Belgesi

        return fastAccess;
    }
}

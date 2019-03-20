package info.pluggabletransports.aptds;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import info.pluggabletransports.dispatch.BridgeConfig;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BridgeConfigParserTest {

    public static final String[] EXAMPLE_URLS = {
            "Bridge 168.80.66.94:443 0BCF64A49F1F1414EA1CE6D7938D19E0198A41D5",
            "bridge://168.80.66.94:443/0BCF64A49F1F1414EA1CE6D7938D19E0198A41D5",
            "http://bridge.onion/0BCF64A49F1F1414EA1CE6D7938D19E0198A41D5?ip=168.80.66.94&orport=443&",
            "https://bridges.torproject.org/0BCF64A49F1F1414EA1CE6D7938D19E0198A41D5?ip=168.80.66.94&orport=443&",

            "Bridge 18.152.86.23:9001 B8B46245BA58DBA4FFEF1A47D9051E6BD450AE1B",
            "bridge://18.152.86.23:9001/B8B46245BA58DBA4FFEF1A47D9051E6BD450AE1B",
            "http://bridge.onion/B8B46245BA58DBA4FFEF1A47D9051E6BD450AE1B?transport&ip=18.152.86.23&orport=9001&",
            "https://bridges.torproject.org/B8B46245BA58DBA4FFEF1A47D9051E6BD450AE1B?ip=18.152.86.23&orport=9001&",

            "Bridge 189.163.13.104:23750 F3CFDB00C5437EC4478529FEDEE194AC70BA87D5",
            "bridge://189.163.13.104:23750/F3CFDB00C5437EC4478529FEDEE194AC70BA87D5",
            "http://bridge.onion/F3CFDB00C5437EC4478529FEDEE194AC70BA87D5?ip=189.163.13.104&orport=23750&",
            "https://bridges.torproject.org/F3CFDB00C5437EC4478529FEDEE194AC70BA87D5?transport=&ip=189.163.13.104&orport=23750&",

            "Bridge meek_lite 0.0.2.0:2 97700DFE9F483596DDA6264C4D7DF7641E1E39CE url=https://meek.azureedge.net/ front=ajax.aspnetcdn.com",
            "bridge://meek_lite@0.0.2.0:2/97700DFE9F483596DDA6264C4D7DF7641E1E39CE?url=https%3A%2F%2Fmeek.azureedge.net%2F&front=ajax.aspnetcdn.com",
            "http://bridge.onion/97700DFE9F483596DDA6264C4D7DF7641E1E39CE?transport=meek_lite&ip=0.0.2.0&orport=2&url=https%3A%2F%2Fmeek.azureedge.net%2F&front=ajax.aspnetcdn.com",
            "https://bridges.torproject.org/97700DFE9F483596DDA6264C4D7DF7641E1E39CE?transport=meek_lite&ip=0.0.2.0&orport=2&url=https%3A%2F%2Fmeek.azureedge.net%2F&front=ajax.aspnetcdn.com",

            "Bridge obfs4 104.224.78.19:443 FD9DAEE45A2FDF70D462914A75ADE99A29957920 cert=LSOd9qOffpIFM4az+ueou7sY0eQRAsI/joW4QgCl/LSDo2ecQzAQHNu281oAivLDZuTQNA iat-mode=0",
            "bridge://obfs4@104.224.78.19:443/FD9DAEE45A2FDF70D462914A75ADE99A29957920?cert=LSOd9qOffpIFM4az%2Bueou7sY0eQRAsI%2FjoW4QgCl%2FLSDo2ecQzAQHNu281oAivLDZuTQNA&iat-mode=0",
            "http://bridge.onion/FD9DAEE45A2FDF70D462914A75ADE99A29957920?transport=obfs4&ip=104.224.78.19&orport=443&cert=LSOd9qOffpIFM4az%2Bueou7sY0eQRAsI%2FjoW4QgCl%2FLSDo2ecQzAQHNu281oAivLDZuTQNA&iat-mode=0",
            "https://bridges.torproject.org/FD9DAEE45A2FDF70D462914A75ADE99A29957920?transport=obfs4&ip=104.224.78.19&orport=443&cert=LSOd9qOffpIFM4az%2Bueou7sY0eQRAsI%2FjoW4QgCl%2FLSDo2ecQzAQHNu281oAivLDZuTQNA&iat-mode=0",

            "Bridge obfs4 107.160.7.24:443 7A0904F6D182B81BEFE0DEDAFEC974494672627B cert=a5/IlZMnDvb8d92LTHMfsBIgL7QlDLPiXiLwe85uedC80mGD0QerygzmsWnMEdwG9ER9Eg iat-mode=0",
            "bridge://obfs4@107.160.7.24:443/7A0904F6D182B81BEFE0DEDAFEC974494672627B?cert=a5%2FIlZMnDvb8d92LTHMfsBIgL7QlDLPiXiLwe85uedC80mGD0QerygzmsWnMEdwG9ER9Eg&iat-mode=0",
            "http://bridge.onion/7A0904F6D182B81BEFE0DEDAFEC974494672627B?transport=obfs4&ip=107.160.7.24&orport=443&cert=a5%2FIlZMnDvb8d92LTHMfsBIgL7QlDLPiXiLwe85uedC80mGD0QerygzmsWnMEdwG9ER9Eg&iat-mode=0",
            "https://bridges.torproject.org/7A0904F6D182B81BEFE0DEDAFEC974494672627B?transport=obfs4&ip=107.160.7.24&orport=443&cert=a5%2FIlZMnDvb8d92LTHMfsBIgL7QlDLPiXiLwe85uedC80mGD0QerygzmsWnMEdwG9ER9Eg&iat-mode=0",

            "Bridge obfs4 24.95.77.155:8080 05CF7049A8AC95A26E8F4611EF9D9625F0589990 cert=Fy8xOhAY1Rm+V4yqv//gYDF9duiYgegERNMFO1Rdk7h/ntM+QtZqmzjdu7o0rx9nz99OFg iat-mode=0",
            "bridge://obfs4@24.95.77.155:8080/05CF7049A8AC95A26E8F4611EF9D9625F0589990?cert=Fy8xOhAY1Rm%2BV4yqv%2F%2FgYDF9duiYgegERNMFO1Rdk7h%2FntM%2BQtZqmzjdu7o0rx9nz99OFg&iat-mode=0",
            "http://bridge.onion/05CF7049A8AC95A26E8F4611EF9D9625F0589990?transport=obfs4&ip=24.95.77.155&orport=8080&cert=Fy8xOhAY1Rm%2BV4yqv%2F%2FgYDF9duiYgegERNMFO1Rdk7h%2FntM%2BQtZqmzjdu7o0rx9nz99OFg&iat-mode=0",
            "https://bridges.torproject.org/05CF7049A8AC95A26E8F4611EF9D9625F0589990?transport=obfs4&ip=24.95.77.155&orport=8080&cert=Fy8xOhAY1Rm%2BV4yqv%2F%2FgYDF9duiYgegERNMFO1Rdk7h%2FntM%2BQtZqmzjdu7o0rx9nz99OFg&iat-mode=0",

            "Bridge obfs4 72.17.139.46:40687 FF41456A5C061DF69E4654F4858199911860CF81 cert=sL0vJhsVKuHO0k7xhOpg/UUHZuftbAQPm0qKUrc9sQWyLWfSNeKI0RzhL+a9TpLMaIoURg iat-mode=0",
            "bridge://obfs4@72.17.139.46:40687/FF41456A5C061DF69E4654F4858199911860CF81?cert=sL0vJhsVKuHO0k7xhOpg%2FUUHZuftbAQPm0qKUrc9sQWyLWfSNeKI0RzhL%2Ba9TpLMaIoURg&iat-mode=0",
            "http://bridge.onion/FF41456A5C061DF69E4654F4858199911860CF81?transport=obfs4&ip=72.17.139.46&orport=40687&cert=sL0vJhsVKuHO0k7xhOpg%2FUUHZuftbAQPm0qKUrc9sQWyLWfSNeKI0RzhL%2Ba9TpLMaIoURg&iat-mode=0",
            "https://bridges.torproject.org/FF41456A5C061DF69E4654F4858199911860CF81?transport=obfs4&ip=72.17.139.46&orport=40687&cert=sL0vJhsVKuHO0k7xhOpg%2FUUHZuftbAQPm0qKUrc9sQWyLWfSNeKI0RzhL%2Ba9TpLMaIoURg&iat-mode=0",

            "Bridge obfs4 78.215.187.186:45675 AE907EE5FAA5D0D27E0C83EFA6ADF8E79FCC0FF1 cert=/TRjMo+RinKaixARMjMtZZBhystaBe+aDaapPrbiITFtWx3M/AJcvpjHjO54tJqLd1+IWQ iat-mode=0",
            "bridge://obfs4@78.215.187.186:45675/AE907EE5FAA5D0D27E0C83EFA6ADF8E79FCC0FF1?cert=%2FTRjMo%2BRinKaixARMjMtZZBhystaBe%2BaDaapPrbiITFtWx3M%2FAJcvpjHjO54tJqLd1%2BIWQ&iat-mode=0",
            "http://bridge.onion/AE907EE5FAA5D0D27E0C83EFA6ADF8E79FCC0FF1?transport=obfs4&ip=78.215.187.186&orport=45675&cert=%2FTRjMo%2BRinKaixARMjMtZZBhystaBe%2BaDaapPrbiITFtWx3M%2FAJcvpjHjO54tJqLd1%2BIWQ&iat-mode=0",
            "https://bridges.torproject.org/AE907EE5FAA5D0D27E0C83EFA6ADF8E79FCC0FF1?transport=obfs4&ip=78.215.187.186&orport=45675&cert=%2FTRjMo%2BRinKaixARMjMtZZBhystaBe%2BaDaapPrbiITFtWx3M%2FAJcvpjHjO54tJqLd1%2BIWQ&iat-mode=0",

            "Bridge obfs4 79.136.160.201:46501 66AC975BF7CB429D057AE07FC0312C57D61BAEC1 cert=dCtn9Ya8z+R8YQikdWgC3XTAt58z5Apnm95QHrJwnhFSdnphPPEz+NMm6OawWc2srKLjJg iat-mode=0",
            "bridge://obfs4@79.136.160.201:46501/66AC975BF7CB429D057AE07FC0312C57D61BAEC1?cert=dCtn9Ya8z%2BR8YQikdWgC3XTAt58z5Apnm95QHrJwnhFSdnphPPEz%2BNMm6OawWc2srKLjJg&iat-mode=0",
            "http://bridge.onion/66AC975BF7CB429D057AE07FC0312C57D61BAEC1?transport=obfs4&ip=79.136.160.201&orport=46501&cert=dCtn9Ya8z%2BR8YQikdWgC3XTAt58z5Apnm95QHrJwnhFSdnphPPEz%2BNMm6OawWc2srKLjJg&iat-mode=0",
            "https://bridges.torproject.org/66AC975BF7CB429D057AE07FC0312C57D61BAEC1?transport=obfs4&ip=79.136.160.201&orport=46501&cert=dCtn9Ya8z%2BR8YQikdWgC3XTAt58z5Apnm95QHrJwnhFSdnphPPEz%2BNMm6OawWc2srKLjJg&iat-mode=0",

            "Bridge obfs4 85.11.218.129:443 4C4441484D06047C1A12F768FC7E7268F2E6DD49 cert=FFKeJPokZXigyKpn+E/iKim/FwNEiIdifbHfaXQmyu1QpSHtNlruAIWebci9m8Yb0tQUOw iat-mode=0",
            "bridge://obfs4@85.11.218.129:443/4C4441484D06047C1A12F768FC7E7268F2E6DD49?cert=FFKeJPokZXigyKpn%2BE%2FiKim%2FFwNEiIdifbHfaXQmyu1QpSHtNlruAIWebci9m8Yb0tQUOw&iat-mode=0",
            "http://bridge.onion/4C4441484D06047C1A12F768FC7E7268F2E6DD49?transport=obfs4&ip=85.11.218.129&orport=443&cert=FFKeJPokZXigyKpn%2BE%2FiKim%2FFwNEiIdifbHfaXQmyu1QpSHtNlruAIWebci9m8Yb0tQUOw&iat-mode=0",
            "https://bridges.torproject.org/4C4441484D06047C1A12F768FC7E7268F2E6DD49?transport=obfs4&ip=85.11.218.129&orport=443&cert=FFKeJPokZXigyKpn%2BE%2FiKim%2FFwNEiIdifbHfaXQmyu1QpSHtNlruAIWebci9m8Yb0tQUOw&iat-mode=0",

            "Bridge obfs4 94.206.82.75:9443 8991E2D5D9751E0AE9A5D2262F894FF98417AF41 cert=ZbzndskJ9+clCg5FcfRkF2zJMqNZDvLFPlJ9TYKKwZMq69DzDnZaqcMlle7udxTWrYR+Rg iat-mode=0",
            "bridge://obfs4@94.206.82.75:9443/8991E2D5D9751E0AE9A5D2262F894FF98417AF41?cert=ZbzndskJ9%2BclCg5FcfRkF2zJMqNZDvLFPlJ9TYKKwZMq69DzDnZaqcMlle7udxTWrYR%2BRg&iat-mode=0",
            "http://bridge.onion/8991E2D5D9751E0AE9A5D2262F894FF98417AF41?transport=obfs4&ip=94.206.82.75&orport=9443&cert=ZbzndskJ9%2BclCg5FcfRkF2zJMqNZDvLFPlJ9TYKKwZMq69DzDnZaqcMlle7udxTWrYR%2BRg&iat-mode=0",
            "https://bridges.torproject.org/8991E2D5D9751E0AE9A5D2262F894FF98417AF41?transport=obfs4&ip=94.206.82.75&orport=9443&cert=ZbzndskJ9%2BclCg5FcfRkF2zJMqNZDvLFPlJ9TYKKwZMq69DzDnZaqcMlle7udxTWrYR%2BRg&iat-mode=0",
    };

    public static final String SHARE_URL_FROM_ORBOT =
            "bridge://Bridge+meek_lite+0.0.2.0%3A2+97700DFE9F483596DDA6264C4D7DF7641E1E39CE+url%3Dhttps%3A%2F%2Fmeek.azureedge.net%2F+front%3Dajax.aspnetcdn.com%0ABridge+obfs4+104.224.78.19%3A443+FD9DAEE45A2FDF70D462914A75ADE99A29957920+cert%3DLSOd9qOffpIFM4az%2Bueou7sY0eQRAsI%2FjoW4QgCl%2FLSDo2ecQzAQHNu281oAivLDZuTQNA+iat-mode%3D0%0ABridge+obfs4+85.11.218.129%3A443+4C4441484D06047C1A12F768FC7E7268F2E6DD49+cert%3DFFKeJPokZXigyKpn%2BE%2FiKim%2FFwNEiIdifbHfaXQmyu1QpSHtNlruAIWebci9m8Yb0tQUOw+iat-mode%3D0%0ABridge+obfs4+24.95.77.155%3A8080+05CF7049A8AC95A26E8F4611EF9D9625F0589990+cert%3DFy8xOhAY1Rm%2BV4yqv%2F%2FgYDF9duiYgegERNMFO1Rdk7h%2FntM%2BQtZqmzjdu7o0rx9nz99OFg+iat-mode%3D0%0ABridge+obfs4+94.206.82.75%3A9443+8991E2D5D9751E0AE9A5D2262F894FF98417AF41+cert%3DZbzndskJ9%2BclCg5FcfRkF2zJMqNZDvLFPlJ9TYKKwZMq69DzDnZaqcMlle7udxTWrYR%2BRg+iat-mode%3D0%0ABridge+obfs4+72.17.139.46%3A40687+FF41456A5C061DF69E4654F4858199911860CF81+cert%3DsL0vJhsVKuHO0k7xhOpg%2FUUHZuftbAQPm0qKUrc9sQWyLWfSNeKI0RzhL%2Ba9TpLMaIoURg+iat-mode%3D0%0ABridge+obfs4+78.215.187.186%3A45675+AE907EE5FAA5D0D27E0C83EFA6ADF8E79FCC0FF1+cert%3D%2FTRjMo%2BRinKaixARMjMtZZBhystaBe%2BaDaapPrbiITFtWx3M%2FAJcvpjHjO54tJqLd1%2BIWQ+iat-mode%3D0%0ABridge+obfs4+107.160.7.24%3A443+7A0904F6D182B81BEFE0DEDAFEC974494672627B+cert%3Da5%2FIlZMnDvb8d92LTHMfsBIgL7QlDLPiXiLwe85uedC80mGD0QerygzmsWnMEdwG9ER9Eg+iat-mode%3D0%0ABridge+obfs4+79.136.160.201%3A46501+66AC975BF7CB429D057AE07FC0312C57D61BAEC1+cert%3DdCtn9Ya8z%2BR8YQikdWgC3XTAt58z5Apnm95QHrJwnhFSdnphPPEz%2BNMm6OawWc2srKLjJg+iat-mode%3D0%0A";

    public static final String EMAIL_FROM_BRIDGES_TORPROJECT_ORG =
            "  79.136.160.201:46501 66AC975BF7CB429D057AE07FC0312C57D61BAEC1\n  85.11.218.129:443 4C4441484D06047C1A12F768FC7E7268F2E6DD49\n  94.206.82.75:9443 8991E2D5D9751E0AE9A5D2262F894FF98417AF41\n";

    public static final String QR_SCAN_FROM_BRIDGES_TORPROJECT_ORG =
            "['79.136.160.201:46501 66AC975BF7CB429D057AE07FC0312C57D61BAEC1', '85.11.218.129:443 4C4441484D06047C1A12F768FC7E7268F2E6DD49', '94.206.82.75:9443 8991E2D5D9751E0AE9A5D2262F894FF98417AF41']";

    @Test
    public void testParsingBridgeURLs() throws Exception {
        for (int i = 0; i < EXAMPLE_URLS.length; i += 4) {
            String bridgeLine = EXAMPLE_URLS[i];
            System.out.println("torrc: " + bridgeLine);
            for (int j = 1; j < 4; j++) {
                String url = EXAMPLE_URLS[i + j];
                URI javaNetUri = new URI(url).normalize();
                String parsedBridgeLine = BridgeConfig.toBridgeLine(javaNetUri);
                assertEquals(bridgeLine, parsedBridgeLine);
                if (url.startsWith("bridge:")) {
                    assertEquals(javaNetUri, BridgeConfig.toBridgeURI(parsedBridgeLine));
                }
                Uri uri = Uri.parse(url);
                assertEquals(bridgeLine, BridgeConfig.toBridgeLine(uri));
                if (j > 1) { // java.net.URL does not support bridge: URI schemes by default
                    URL javaNetUrl = new URL(url);
                    assertEquals(bridgeLine, BridgeConfig.toBridgeLine(javaNetUrl));
                }
            }
        }
    }
}

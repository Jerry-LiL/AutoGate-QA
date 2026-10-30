import pytest
from pages.track_screen import TrackScreen
from pages.settings_screen import SettingsScreen


@pytest.mark.mobile
class TestMobileTrackApp:

    def test_track_screen_header_rendered(self, driver):
        track_screen = TrackScreen(driver)
        assert track_screen.is_header_visible(), "Track screen title header should be visible"
        assert track_screen.is_settings_button_visible(), "Settings button should be visible in header"

    def test_navigate_to_settings_screen(self, driver):
        track_screen = TrackScreen(driver)
        track_screen.open_settings()

        settings_screen = SettingsScreen(driver)
        assert settings_screen.is_reset_button_visible(), "Reset server URL button should be visible on settings"
        assert settings_screen.is_save_button_visible(), "Save server URL button should be visible on settings"

    def test_settings_reset_server_url(self, driver):
        track_screen = TrackScreen(driver)
        track_screen.open_settings()

        settings_screen = SettingsScreen(driver)
        settings_screen.click_reset_default()
        assert settings_screen.is_save_button_visible(), "Settings should remain interactive after reset"

    def test_settings_custom_server_url(self, driver):
        track_screen = TrackScreen(driver)
        track_screen.open_settings()

        settings_screen = SettingsScreen(driver)
        settings_screen.set_server_url("http://10.0.2.2:3000")
        settings_screen.click_save_and_resync()

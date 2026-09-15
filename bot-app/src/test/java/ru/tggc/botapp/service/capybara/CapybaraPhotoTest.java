package ru.tggc.botapp.service.capybara;

import com.pengrad.telegrambot.model.PhotoSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CapybaraPhotoTest {
    @Test
    void choosesLargestAreaRegardlessOfTelegramArrayOrder() {
        PhotoSize small = size(90, 90);
        PhotoSize large = size(1600, 1200);
        PhotoSize medium = size(800, 600);
        assertSame(large, CapybaraProfileService.largestPhoto(new PhotoSize[]{small, large, medium}));
    }

    @Test
    void rejectsEmptySizes() {
        assertThrows(IllegalArgumentException.class, () -> CapybaraProfileService.largestPhoto(new PhotoSize[0]));
    }

    private PhotoSize size(int width, int height) {
        PhotoSize photo = mock(PhotoSize.class);
        when(photo.width()).thenReturn(width);
        when(photo.height()).thenReturn(height);
        return photo;
    }
}

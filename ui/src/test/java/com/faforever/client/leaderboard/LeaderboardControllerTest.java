package com.faforever.client.leaderboard;

import com.faforever.client.game.KnownFeaturedMod;
import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class LeaderboardControllerTest extends AbstractPlainJavaFxTest {

  private LeaderboardController instance;

  @Mock
  private LeaderboardService leaderboardService;
  @Mock
  private NotificationService notificationService;
  @Mock
  private ReportingService reportingService;
  @Mock
  private I18n i18n;

  @Before
  public void setUp() throws Exception {
    instance = new LeaderboardController(leaderboardService, notificationService, i18n, reportingService);

    loadFxml("theme/leaderboard/leaderboard.fxml", clazz -> instance);
  }

  @Test
  public void testOnDisplay() throws Exception {
    when(leaderboardService.getEntries(KnownFeaturedMod.LADDER_1V1.getTechnicalName())).thenReturn(CompletableFuture.completedFuture(Arrays.asList(
        new LeaderboardEntry(), new LeaderboardEntry()
    )));

    CountDownLatch loadedLatch = new CountDownLatch(1);
    instance.ratingTable.itemsProperty().addListener(observable -> loadedLatch.countDown());
    instance.setLeaderboardName(KnownFeaturedMod.LADDER_1V1.getTechnicalName());

    instance.display(new OpenLadder1v1LeaderboardEvent());

    assertTrue(loadedLatch.await(3, TimeUnit.SECONDS));
    verifyZeroInteractions(notificationService);
  }

  @Test
  public void testFilterByNamePlayerExactMatch() throws Exception {
    LeaderboardEntry entry1 = new LeaderboardEntry();
    entry1.setPlayerName("Aa");
    LeaderboardEntry entry2 = new LeaderboardEntry();
    entry2.setPlayerName("Ab");

    when(leaderboardService.getEntries(KnownFeaturedMod.LADDER_1V1.getTechnicalName())).thenReturn(CompletableFuture.completedFuture(Arrays.asList(
        entry1, entry2
    )));
    instance.setLeaderboardName(KnownFeaturedMod.LADDER_1V1.getTechnicalName());
    instance.display(new OpenLadder1v1LeaderboardEvent());

    assertThat(instance.ratingTable.getSelectionModel().getSelectedItem(), nullValue());

    instance.searchTextField.setText("aa");
    assertThat(instance.ratingTable.getItems(), hasSize(2));
    assertThat(instance.ratingTable.getSelectionModel().getSelectedItem().getPlayerName(), is("Aa"));
  }

  @Test
  public void testFilterByNamePlayerPartialMatch() throws Exception {
    LeaderboardEntry entry1 = new LeaderboardEntry();
    entry1.setPlayerName("Aa");
    LeaderboardEntry entry2 = new LeaderboardEntry();
    entry2.setPlayerName("Ab");

    when(leaderboardService.getEntries(KnownFeaturedMod.LADDER_1V1.getTechnicalName())).thenReturn(CompletableFuture.completedFuture(Arrays.asList(
        entry1, entry2
    )));
    instance.setLeaderboardName(KnownFeaturedMod.LADDER_1V1.getTechnicalName());
    instance.display(new OpenLadder1v1LeaderboardEvent());

    assertThat(instance.ratingTable.getSelectionModel().getSelectedItem(), nullValue());

    instance.searchTextField.setText("b");
    assertThat(instance.ratingTable.getItems(), hasSize(2));
    assertThat(instance.ratingTable.getSelectionModel().getSelectedItem().getPlayerName(), is("Ab"));
  }

  @Test
  public void testGetRoot() throws Exception {
    assertThat(instance.getRoot(), is(instance.leaderboardRoot));
    assertThat(instance.getRoot().getParent(), is(nullValue()));
  }
}
/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.client.ui.zone;

import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;

import java.util.List;
import java.util.function.Supplier;

public class PlayerView {
  private final Player.Role role;
  // [PNICHOLS04] This `Supplier` supports lazy initialization of the token list, which in turn avoids list iterations
  // that occurred on every rendering pass, when the `PlayerView` was created.
  private Supplier<List<Token>> tokenSupplier; // Optional
  private List<Token> tokens;

  // Optimization
  private String hash;

  public PlayerView(Player.Role role) {
    this(role, null);
  }

  public PlayerView(Player.Role role, Supplier<List<Token>> withTokenSupplier) {
    this.role = role;
    // [PNICHOLS04]  Implements lazy initialization.  See comment on `tokenSupplier` field declaration.
    tokenSupplier = withTokenSupplier;
    // this.tokens = tokens != null && !tokens.isEmpty() ? tokens : null;
    // hash = calculateHashcode();
  }

  public Player.Role getRole() {
    return role;
  }

  public boolean isGMView() {
    return role == Player.Role.GM;
  }

  /**
   * Returns a value indicating whether the user is viewing the {@link net.rptools.maptool.model.Zone} as a player.
   * <p>This is a convenience method, which is provided to improve the readability of the code render pathways.  Its
   * value is equivalent to {@code !playerView.isGMView()}.</p>
   */
  public boolean isPCView() { return role != Player.Role.GM; }

  public List<Token> getTokens() {
    // [PNICHOLS04]  Implements lazy initialization.  See comment on `tokenSupplier` field declaration.
    if(tokens == null || tokenSupplier != null) {
      this.tokens = tokenSupplier.get();
    }
    return tokens;
  }

  public boolean isUsingTokenView() {
    return tokens != null;
  }

  @Override
  public int hashCode() {
    // [PNICHOLS04]  Implements lazy initialization.  See comment on `tokenSupplier` field declaration.
    if(hash == null) {
      hash = calculateHashcode();
    }
    return hash.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    // [PNICHOLS04]  A null check is not required on `instanceof`.  I have removed the one that was here.
    if (!(obj instanceof PlayerView)) {
      return false;
    }
    PlayerView other = (PlayerView) obj;
    // [PNICHOLS04]  Implements lazy initialization.  See comment on `tokenSupplier` field declaration.
    return hashCode() == other.hashCode();
  }

  private String calculateHashcode() {
    StringBuilder builder = new StringBuilder();
    builder.append(role);
    if (tokens != null) {
      for (Token token : tokens) {
        builder.append(token.getId());
      }
    }
    return builder.toString();
  }
}

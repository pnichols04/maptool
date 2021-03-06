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
package net.rptools.maptool.client;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import net.rptools.maptool.model.Token;

public class TransferableToken implements Transferable {
  public static final DataFlavor dataFlavor = new DataFlavor(Token.class, "Token");

  private Token token;

  public TransferableToken(Token token) {
    this.token = token;
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    return token;
  }

  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] {dataFlavor};
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(dataFlavor);
  }
}

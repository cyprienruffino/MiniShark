package ovh.soup.minishark.views;

import android.app.Activity;
import android.os.Bundle;

import ovh.soup.minishark.R;

/**
 * Created by cyprien on 28/09/16.
 *
 * This file is part of Minishark.
 *
 *   Minishark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Minishark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Minishark.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Project repository : https://github.com/Moi4167/Minishark
 */

public class CreditsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);
    }

}

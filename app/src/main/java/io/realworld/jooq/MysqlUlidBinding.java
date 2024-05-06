package io.realworld.jooq;

import java.io.Serial;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;

import org.jetbrains.annotations.Nullable;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import de.huxhorn.sulky.ulid.ULID;

/**
 * T: BINARY(16) = byte[] (?) <br>
 * U: ULID
 *
 * @see <a href="https://github.com/huxi/sulky/tree/27c8d809f2e3af6eafa9c7324f158cd9b482b351/sulky-ulid">sulky-ulid</a>
 */
public class MysqlUlidBinding implements Binding<byte[], ULID.Value> {

    @Serial
    private static final long serialVersionUID = -3337341252809572876L;

    @Override
    public Converter<byte[], ULID.Value> converter() {
        return new Converter<>() {
            @Serial
            private static final long serialVersionUID = 5792124078876128755L;

            @Override
            @Nullable
            public ULID.Value from(byte[] bytes) {
                if (bytes == null) {
                    return null;
                } else {
                    return ULID.fromBytes(bytes);
                }
            }

            @Override
//            @Nullable
            public byte[] to(ULID.Value ulidValue) {
                return ulidValue.toBytes();
            }

            @Override
            public Class<byte[]> fromType() {
                return byte[].class;
            }

            @Override
            public Class<ULID.Value> toType() {
                return ULID.Value.class;
            }
        };
    }

    @Override
    public void sql(BindingSQLContext<ULID.Value> ctx) throws SQLException {
        if (ctx.render().paramType() == ParamType.INLINED) {
            ctx.render().visit(DSL.inline(ctx.convert(converter()).value())).sql("::ulid");
        } else {
            ctx.render().sql(ctx.variable()).sql("::ulid");
        }
    }

    @Override
    public void register(BindingRegisterContext<ULID.Value> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.BINARY);
    }

    @Override
    public void set(BindingSetStatementContext<ULID.Value> ctx) throws SQLException {
        final byte[] bytes = ctx.convert(converter()).value();
        ctx.statement().setBytes(ctx.index(), bytes);
    }

    @Override
    public void set(BindingSetSQLOutputContext<ULID.Value> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetResultSetContext<ULID.Value> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getBytes(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<ULID.Value> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getBytes(ctx.index()));
    }

    @Override
    public void get(BindingGetSQLInputContext<ULID.Value> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
